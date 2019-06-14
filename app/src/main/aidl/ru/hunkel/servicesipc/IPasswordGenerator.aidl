// IPasswordGenerator.aidl
package ru.hunkel.servicesipc;

// Declare any non-default types here with import statements

interface IPasswordGenerator {

    String generatePassword();
    String generatePasswordWithFixedLenght(int lenght);

}
