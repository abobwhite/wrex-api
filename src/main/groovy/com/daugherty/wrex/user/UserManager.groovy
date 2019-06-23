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

    if (!user) {
      throw new WrexException(ERROR_CODE.NOT_FOUND)
    }

    user
  }

  List<User> getUsers() {
    userRepository.findAll()
  }

  User modifyUser(String userId, User updatedUser) {
    def user = getUserById(userId) // throws if not found

    if (updatedUser.firstName) {
      user.firstName = updatedUser.firstName
    }

    if (updatedUser.lastName) {
      user.lastName = updatedUser.lastName
    }

    if (updatedUser.preferences) {
      user.preferences = updatedUser.preferences
    }

    updateUser(user)
  }

  User createUser(User user) {
    if(!user.preferences) {
      user.preferences = new UserPreferences()
    }

    userRepository.insert(user)
  }

  User updateUser(User user) {
    userRepository.save(user)
  }
}
