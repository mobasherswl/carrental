package com.epam.server.service;

import com.epam.common.dto.RentalClassDto;
import com.epam.server.model.RentalClass;
import com.epam.server.repo.RentalClassRepository;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.dao.DataIntegrityViolationException;

import java.lang.reflect.Type;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RentalClassServiceImplTest {
    @InjectMocks
    private RentalClassServiceImpl rentalClassService;
    @Mock
    private RentalClassRepository rentalClassRepository;
    @Mock
    private ModelMapper modelMapper;
    private Type listRentalClassDtoType;
    private Type listRentalClassType;

    @Before
    public void init() {
        listRentalClassDtoType = new TypeToken<List<RentalClassDto>>() {
        }.getType();
        listRentalClassType = new TypeToken<List<RentalClass>>(){}.getType();
    }

    @Test
    public void add() {
        RentalClassDto newRentalClassDto = mock(RentalClassDto.class);
        RentalClassDto savedRentalClassDto = mock(RentalClassDto.class);
        RentalClass rentalClass = mock(RentalClass.class);

        when(savedRentalClassDto.getId()).thenReturn(1L);
        when(modelMapper.map(newRentalClassDto, RentalClass.class)).thenReturn(rentalClass);
        when(rentalClassRepository.save(rentalClass)).thenReturn(rentalClass);
        when(modelMapper.map(rentalClass, RentalClassDto.class)).thenReturn(savedRentalClassDto);
        assertEquals(1L, rentalClassService.add(newRentalClassDto).getId().longValue());
        verify(modelMapper, times(1)).map(newRentalClassDto, RentalClass.class);
        verify(modelMapper, times(1)).map(rentalClass, RentalClassDto.class);
        verify(rentalClassRepository, times(1)).save(rentalClass);
    }

    @Test(expected = DataIntegrityViolationException.class)
    public void addWithSameName() {
        RentalClassDto newRentalClassDto = mock(RentalClassDto.class);
        RentalClassDto savedRentalClassDto = mock(RentalClassDto.class);
        RentalClass rentalClass = mock(RentalClass.class);

        when(modelMapper.map(newRentalClassDto, RentalClass.class)).thenReturn(rentalClass);
        when(rentalClassRepository.save(rentalClass)).thenThrow(DataIntegrityViolationException.class);
        when(modelMapper.map(rentalClass, RentalClassDto.class)).thenReturn(savedRentalClassDto);
        rentalClassService.add(newRentalClassDto);
        verify(modelMapper, times(1)).map(newRentalClassDto, RentalClass.class);
        verify(modelMapper, times(0)).map(rentalClass, RentalClassDto.class);
        verify(rentalClassRepository, times(1)).save(rentalClass);
    }

    @Test
    public void findAll() {
        List<RentalClassDto> searchedRentalClassDtoList = mock(List.class);
        List<RentalClass> rentalClassList = mock(List.class);

        when(searchedRentalClassDtoList.size()).thenReturn(1);
        when(rentalClassRepository.findAll()).thenReturn(rentalClassList);
        when(modelMapper.map(rentalClassList, listRentalClassDtoType)).thenReturn(searchedRentalClassDtoList);
        assertEquals(rentalClassService.findAll().size(), 1);
        verify(modelMapper, times(1)).map(rentalClassList, listRentalClassDtoType);
        verify(rentalClassRepository, times(1)).findAll();
    }

    @Test
    public void addAll() {
        List<RentalClassDto> saveRentalClassDtoList = mock(List.class);
        List<RentalClass> rentalClassList = mock(List.class);

        when(rentalClassRepository.save(rentalClassList)).thenReturn(rentalClassList);
        when(rentalClassService.add(saveRentalClassDtoList)).thenReturn(saveRentalClassDtoList);
        when(modelMapper.map(rentalClassList, listRentalClassDtoType)).thenReturn(saveRentalClassDtoList);
        when(modelMapper.map(saveRentalClassDtoList, listRentalClassType)).thenReturn(rentalClassList);
        assertNotNull(rentalClassService.add(saveRentalClassDtoList));
        verify(modelMapper).map(rentalClassList, listRentalClassDtoType);
        verify(rentalClassRepository, times(1)).save(rentalClassList);
    }
}
