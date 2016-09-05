package com.epam.server.service;

import com.epam.common.dto.RentalClassDto;
import com.epam.server.model.RentalClass;
import com.epam.server.repo.BaseRepository;
import com.epam.common.service.RentalClassService;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.util.List;

@Service
public class RentalClassServiceImpl implements RentalClassService {
    private Type rentalClassDtoListType = new TypeToken<List<RentalClassDto>>(){}.getType();
    private Type rentalClassListType = new TypeToken<List<RentalClass>>() {
    }.getType();
    private BaseRepository<RentalClass, Long> rentalClassRepository;
    private ModelMapper modelMapper;

    @Autowired
    public RentalClassServiceImpl(BaseRepository<RentalClass, Long> rentalClassRepository, ModelMapper modelMapper) {
        this.rentalClassRepository = rentalClassRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RentalClassDto add(RentalClassDto rentalClassDto) {
        return modelMapper.map(
                rentalClassRepository.save(modelMapper.map(rentalClassDto, RentalClass.class)),
                RentalClassDto.class);
    }

    @Override
    public List<RentalClassDto> add(List<RentalClassDto> list) {
        return modelMapper.map(rentalClassRepository.save((Iterable<RentalClass>) modelMapper.map(list, rentalClassListType)), rentalClassDtoListType);
    }

    @Override
    public List<RentalClassDto> findAll() {
        return modelMapper.map(rentalClassRepository.findAll(), rentalClassDtoListType);
    }
}
