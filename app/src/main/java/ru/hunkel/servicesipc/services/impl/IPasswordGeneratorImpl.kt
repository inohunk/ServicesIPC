package ru.hunkel.servicesipc.services.impl

import ru.hunkel.servicesipc.IPasswordGenerator

class IPasswordGeneratorImpl : IPasswordGenerator.Stub() {

    private val SYMBOLS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789"


    override fun generatePassword(): String {
        var password = String()

        for (i in 0 until 10) {
            password = password.plus(SYMBOLS.random())
        }
        return password
    }

    override fun generatePasswordWithFixedLenght(lenght: Int): String {
        var password = String()

        for (i in 0 until lenght) {
            password = password.plus(SYMBOLS.random())
        }
        return password
    }
}