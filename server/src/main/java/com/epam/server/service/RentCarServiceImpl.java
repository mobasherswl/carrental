package com.epam.server.service;

import com.epam.common.dto.CarDto;
import com.epam.common.dto.RentCarDto;
import com.epam.common.service.RentCarService;
import com.epam.server.model.Car;
import com.epam.server.model.RentCar;
import com.epam.server.model.RentalClass;
import com.epam.server.rentcarstatistics.RentCarStatisticGenerator;
import com.epam.server.rentcarstatistics.RentMinutesPerDay;
import com.epam.server.repo.BaseRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * Created by Ahmed_Khan on 5/10/2016.
 */
@Service
public class RentCarServiceImpl implements RentCarService {

    private BaseRepository<RentCar, Long> rentCarRepository;
    private BaseRepository<Car, Long> carRepository;
    private ModelMapper modelMapper;
    private Clock clock;

    @Autowired
    public RentCarServiceImpl(BaseRepository<RentCar, Long> rentCarRepository, BaseRepository<Car, Long> carRepository, ModelMapper modelMapper, Clock clock) {
        this.rentCarRepository = rentCarRepository;
        this.carRepository = carRepository;
        this.modelMapper = modelMapper;
        this.clock = clock;
    }

    @Override
    public RentCarDto rentOut(RentCarDto rentCarDto) {
        RentCar rentCar = modelMapper.map(rentCarDto, RentCar.class);
        rentCar.setId(null);
        rentCar.setStartDateTime(LocalDateTime.now(clock));
        rentCar.setEndDateTime(null);
        rentCar.setCarRented(Boolean.TRUE);
        rentCar.setVersion(null);
        return modelMapper.map(rentCarRepository.save(rentCar), RentCarDto.class);
    }

    @Override
    public List<CarDto> findAvailableCars() {
        Map<Long, RentCarDto> rentCarDtoMap = StreamSupport.stream(rentCarRepository.findAll().spliterator(), true)
                .filter(rentCar -> rentCar.getEndDateTime() == null)
                .map(rentCar -> modelMapper.map(rentCar, RentCarDto.class))
                .collect(Collectors.toMap(rentCarDto -> rentCarDto.getCarDto().getId(), Function.identity()));
        return StreamSupport.stream(carRepository.findAll().spliterator(), true)
                .filter(
                        car -> !rentCarDtoMap.containsKey(car.getId())
                                || rentCarDtoMap.get(car.getId()).getEndDateTime() != null)
                .map(car -> modelMapper.map(car, CarDto.class)).collect(Collectors.toList());
    }

    @Override
    public List<RentCarDto> findRentedOutCars() {
        return StreamSupport.stream(rentCarRepository.findAll().spliterator(), true)
                .filter(rentCar -> rentCar.getEndDateTime() == null)
                .map(rentCar -> modelMapper.map(rentCar, RentCarDto.class))
                .collect(Collectors.toList());
    }

    @Override
    public RentCarDto rentCarReturn(RentCarDto rentCarDto) {
        RentCar rentCar = rentCarRepository.findOne(rentCarDto.getId());
        rentCar.setEndDateTime(LocalDateTime.now(clock));
        rentCar.setCarRented(null);
        return modelMapper.map(rentCarRepository.save(rentCar), RentCarDto.class);
    }

    @Override
    public List<RentCarDto> getRentHistoryDto(LocalDate fromLocalDate, LocalDate toLocalDate) {
        return getRentHistoryBetween(fromLocalDate, toLocalDate).stream().map(rentCar -> modelMapper.map(rentCar, RentCarDto.class)).collect(Collectors.toList());
    }

    private List<RentCar> getRentHistoryBetween(LocalDate fromLocalDate, LocalDate toLocalDate) {
        return  StreamSupport.stream(rentCarRepository.findAll().spliterator(), true).filter(
                rentCar ->
                        rentCar.getStartDateTime() != null
                                && (rentCar.getStartDateTime().toLocalDate().compareTo(fromLocalDate) >= 0
                                && rentCar.getStartDateTime().toLocalDate().compareTo(toLocalDate) <= 0)
                                && rentCar.getEndDateTime() != null
                                && (rentCar.getEndDateTime().toLocalDate().compareTo(fromLocalDate) >= 0
                                && rentCar.getEndDateTime().toLocalDate().compareTo(toLocalDate) <= 0))
                .collect(Collectors.toList());
    }

    @Override
    public String getRentHistoryStatistics(LocalDate fromLocalDate, LocalDate toLocalDate) {
        RentCarStatisticGenerator rentCarStatisticGenerator = new RentCarStatisticGenerator();
        List<RentCar> rentHistoryBetween = getRentHistoryBetween(fromLocalDate, toLocalDate);
        HashMap<RentalClass, RentMinutesPerDay> mapWithRentalClassAndHoursOfRentForEachDay = rentCarStatisticGenerator.getMapWithRentalClassAndMinutesOfRentForEachDay(rentHistoryBetween);
        RentMinutesPerDay mapWithTimeForEachDayBetweenPeriod = rentCarStatisticGenerator.getMapWithMinutesForEachDayBetweenPeriod(fromLocalDate, toLocalDate);
        return rentCarStatisticGenerator.generateTableWithEarningsAndUtilizationByRentalClass(mapWithRentalClassAndHoursOfRentForEachDay, mapWithTimeForEachDayBetweenPeriod);
    }

}
