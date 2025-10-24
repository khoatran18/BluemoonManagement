package main.java.com.project.service;

import com.project.dto.ApartmentDTO;
import com.project.entity.Apartment;
import com.project.exception.ConflictException;
import com.project.exception.NotFoundException;
import com.project.mapper.ApartmentMapper;
import com.project.repository.ApartmentRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class ApartmentService {

    @Inject
    ApartmentRepository repository;

    public List<ApartmentDTO> getAllApartments() {
        return repository.listAll()
                .stream()
                .map(ApartmentMapper::toDTO)
                .collect(Collectors.toList());
    }

    public ApartmentDTO getApartmentById(Long id) {
        Apartment entity = repository.findById(id);
        if (entity == null)
            throw new NotFoundException("Apartment not found with id: " + id);
        return ApartmentMapper.toDTO(entity);
    }

    @Transactional
    public ApartmentDTO createApartment(ApartmentDTO dto) {
        Apartment existing = repository.findByRoom(dto.building, dto.roomNumber);
        if (existing != null)
            throw new ConflictException("Apartment already exists at " + dto.building + " - " + dto.roomNumber);

        Apartment entity = ApartmentMapper.toEntity(dto);
        repository.persist(entity);
        return ApartmentMapper.toDTO(entity);
    }

    @Transactional
    public ApartmentDTO updateApartment(Long id, ApartmentDTO dto) {
        Apartment entity = repository.findById(id);
        if (entity == null)
            throw new NotFoundException("Apartment not found with id: " + id);

        entity.setBuilding(dto.building);
        entity.setRoomNumber(dto.roomNumber);
        return ApartmentMapper.toDTO(entity);
    }

    @Transactional
    public void deleteApartment(Long id) {
        Apartment entity = repository.findById(id);
        if (entity == null)
            throw new NotFoundException("Apartment not found with id: " + id);
        repository.delete(entity);
    }
}
