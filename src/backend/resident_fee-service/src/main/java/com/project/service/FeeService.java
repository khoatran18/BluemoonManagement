package main.java.com.project.service;

import com.project.dto.FeeDTO;
import com.project.entity.Fee;
import com.project.exception.NotFoundException;
import com.project.mapper.FeeMapper;
import com.project.repository.FeeRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FeeService {

    @Inject
    FeeRepository repository;

    public List<FeeDTO> getAllFees() {
        return repository.listAll().stream()
                .map(FeeMapper::toDTO)
                .collect(Collectors.toList());
    }

    public FeeDTO getFeeById(Long id) {
        Fee fee = repository.findById(id);
        if (fee == null)
            throw new NotFoundException("Fee not found with id: " + id);
        return FeeMapper.toDTO(fee);
    }

    @Transactional
    public FeeDTO createFee(FeeDTO dto) {
        Fee entity = FeeMapper.toEntity(dto);
        repository.persist(entity);
        return FeeMapper.toDTO(entity);
    }

    @Transactional
    public void deleteFee(Long id) {
        Fee fee = repository.findById(id);
        if (fee == null)
            throw new NotFoundException("Fee not found with id: " + id);
        repository.delete(fee);
    }
}
