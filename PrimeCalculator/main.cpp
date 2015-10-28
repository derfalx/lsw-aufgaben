#include <iostream>


#include <gmp.h>
#include <time.h>
#include "4096-Bit Prime/PrimeCalculator.h"

using namespace std;

int main() {
    cout << "Hello, World!" << endl;

    time_t t = time(nullptr);
    PrimeCalculator prime = PrimeCalculator();
    prime.calculatePrime(4096);
    cout << time(nullptr) - t << endl;


    return 0;
}