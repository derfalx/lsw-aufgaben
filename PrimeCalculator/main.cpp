#include <iostream>


#include <gmp.h>
#include "4096-Bit Prime/PrimeCalculator.h"

using namespace std;

int main() {
    cout << "Hello, World!" << endl;


    PrimeCalculator prime = PrimeCalculator();
    prime.calculatePrime(4096);

    return 0;
}