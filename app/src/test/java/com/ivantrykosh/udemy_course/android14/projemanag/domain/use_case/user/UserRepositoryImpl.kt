package com.ivantrykosh.udemy_course.android14.projemanag.domain.use_case.user

import com.ivantrykosh.udemy_course.android14.projemanag.domain.model.User
import com.ivantrykosh.udemy_course.android14.projemanag.domain.repository.UserRepository

object UserRepositoryImpl : UserRepository {
    val testUserId = "testUserId"
    val testEmail = "test@email.com"
    val testUser = User(id = testUserId, email = testEmail)

    val testListOfUsersIds = listOf("testUser1Id", "testUser2Id", "testUser3Id")
    val testListOfUsers = listOf(User(id = testListOfUsersIds[0]), User(id = testListOfUsersIds[1]), User(id = testListOfUsersIds[2]))

    override suspend fun createUser(user: User) {
        if (user.email.isEmpty()) {
            throw Exception("Email of user is incorrect")
        } else if (user.name.isEmpty()) {
            throw Exception("Email of user is incorrect")
        }
    }

    override suspend fun getCurrentUser(): User {
        return testUser
    }

    override suspend fun updateUser(userData: Map<String, Any>) {
        userData.forEach { (key, _) ->
            if (
                key != User.FIELDS.NAME &&
                key != User.FIELDS.IMAGE &&
                key != User.FIELDS.MOBILE &&
                key != User.FIELDS.FCM_TOKEN
                ) {
                throw Exception("User data is incorrect")
            }
        }
    }

    override suspend fun getUserByEmail(email: String): User {
        if (email != testEmail) {
            throw Exception("Email is incorrect")
        }
        return testUser
    }

    override suspend fun getUsersByIds(userIds: List<String>): List<User> {
        return testListOfUsers.filter { userIds.contains(it.id) }
    }
}