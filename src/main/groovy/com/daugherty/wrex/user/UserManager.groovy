package com.daugherty.wrex.user

import com.daugherty.wrex.exception.ERROR_CODE
import com.daugherty.wrex.exception.WrexException
import org.springframework.stereotype.Service

@Service
class UserManager {
  private final UserRepository userRepository

  UserManager(final UserRepository userRepository) {
    this.userRepository = userRepository
  }

  User getUserById(String userId) {
    def user = userRepository.findById(userId).orElse(null)

    if(!user) {
      throw new WrexException(ERROR_CODE.NOT_FOUND)
    }

    user
  }

  List<User> getUsers() {
    userRepository.findAll()
  }

  User saveUser(User user) {
    userRepository.save(user)
  }
}
