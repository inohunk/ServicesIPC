package ru.hunkel.servicesipc.services.impl

import ru.hunkel.servicesipc.IPasswordGenerator

class IPasswordGeneratorImpl : IPasswordGenerator.Stub() {
    override fun generatePassword(): String {
//        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
        return "password"
    }
}