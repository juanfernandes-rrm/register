package br.ufpr.tads.user.register.domain.service;

import br.ufpr.tads.user.register.domain.mapper.BranchMapper;
import br.ufpr.tads.user.register.domain.model.Address;
import br.ufpr.tads.user.register.domain.model.Branch;
import br.ufpr.tads.user.register.domain.repository.BranchRepository;
import br.ufpr.tads.user.register.domain.response.BranchDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Slf4j
@Service
public class BranchService {

    @Autowired
    private BranchRepository branchRepository;

    @Autowired
    private AddressService addressService;

    public List<BranchDTO> getNearbyBranch(String cep, double maxDistanceKm) {
        Address userAddress = addressService.getAddressByCep(cep);

        List<Branch> allBranches = branchRepository.findAll();

        List<BranchDTO> nearbyBranches = new ArrayList<>();
        for (Branch branch : allBranches) {
            Address branchAddress = branch.getAddress();

            double distance = addressService.calculateDistanceBetweenAddresses(userAddress, branchAddress);

            if (distance <= maxDistanceKm) {
                BranchDTO branchDTO = BranchMapper.convertToDTO(branch, distance);
                nearbyBranches.add(branchDTO);
            }
        }

        nearbyBranches.sort(Comparator.comparingDouble(BranchDTO::getDistance));
        return nearbyBranches;
    }
}


