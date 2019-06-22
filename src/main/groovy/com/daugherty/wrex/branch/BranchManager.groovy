package com.daugherty.wrex.branch

import org.springframework.stereotype.Service

@Service
class BranchManager {
  private final BranchRepository branchRepository

  BranchManager(final BranchRepository branchRepository) {
    this.branchRepository = branchRepository
  }

  List<Branch> getBranches() {
    branchRepository.findAll()
  }
}
