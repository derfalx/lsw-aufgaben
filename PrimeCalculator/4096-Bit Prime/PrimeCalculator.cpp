//
// Created by falx on 24.06.15.
//

#include <iostream>
#include <ctime>
#include <math.h>
#include "PrimeCalculator.h"


void PrimeCalculator::calculatePrime ( unsigned int bit )
{
  // create big int and init it with 0
  mpz_class * randomNumber = nullptr;
  bool isPossiblePrime = false;

  for ( int i = 0; i < 100000; i++ )
  {
    randomNumber = this->generateRandomNumber ( bit );
    isPossiblePrime = this->checkAgainstKnownPrimes ( randomNumber );
    if ( isPossiblePrime )
    {
      //* randomNumber = 221;
      std::cout << "found pre sieved possible prime" << std::endl;
      isPossiblePrime = this->millerRabinTest ( randomNumber, 10 );
      if ( isPossiblePrime )
        break;
    }
  }

  mpz_out_str ( stdout, 10, randomNumber->get_mpz_t ( ));
  std::cout << "\n"
      "Is possible prime: " << isPossiblePrime << std::endl;


  //return randomNumber;
}


bool PrimeCalculator::checkAgainstKnownPrimes ( mpz_class * number )
{
  mpz_class * testPrime = new mpz_class ( );
  bool isPossible = true;

  for ( int i = 0; i < this->numberOfPrecalculatedPrimes; i++ )
  {
    *testPrime = this->precalculatedPrimes[i];
    if (( *number % *testPrime ) == 0 )
    {
      isPossible = false;
      break;
    }
  }

  delete testPrime;

  return isPossible;
}


mpz_class * PrimeCalculator::generateRandomNumber ( unsigned int bit )
{
  mp_bitcnt_t bitCount = bit;

  // set up random generator
  gmp_randclass * rand = new gmp_randclass ( gmp_randinit_mt );
  std::srand ( std::time ( 0 ));
  rand->seed ( std::rand ( ));

  mpz_class * randomNumber = new mpz_class ( rand->get_z_bits ( bitCount ));

  delete rand;

  return randomNumber;
}

bool PrimeCalculator::millerRabinTest ( mpz_class * number, int precision )
{
  bool probablyPrime = true;

  // definition we initialize later, to generate base_a
  gmp_randclass * rand = nullptr;

  // represents the base a in the miller-rabin-test formular
  mpz_class * const base_a = new mpz_class();

  // base 2 needed for the miller-rabin-test
  mpz_class * const base_2 = new mpz_class ( );
  *base_2 = 2;

  // represents r in the miller-rabin-test formular
  unsigned long int exponent = 2;

  // represents d in the miller-rabin-test formular
  mpz_class * factor = new mpz_class ( );
  *factor = 1;

  mpz_class * rop = new mpz_class ( );
  *rop = 0;

  mpz_class * number_less = new mpz_class();
  *number_less = ( *number ) - 1;

  while ( true )
  {
    mpz_pow_ui ( rop->get_mpz_t (), base_2->get_mpz_t ( ), exponent );
    if (( *number_less % *rop ) == 0 )
      break;
    else
      exponent++;
  }

  *factor = *number_less / *rop;


  rand = new gmp_randclass ( gmp_randinit_mt );
  std::srand( std::time( 0 ) );
  rand->seed ( std::rand() );


  std::cout << "factor=";
  mpz_out_str ( stdout, 10, factor->get_mpz_t ( ));
  std::cout << std::endl;

  mpz_class * module = new mpz_class();

  for ( int i = 0; i < precision; i++ )
  {
    *base_a = rand->get_z_range ( *number ); // generate a random number from 0 to number - 1
    if ( *base_a < 2 )
    {
      *base_a += 2;

    }

    mpz_powm ( module->get_mpz_t (), base_a->get_mpz_t (), factor->get_mpz_t (), number->get_mpz_t () );

    if ( ! (*module == 1 || *module == *number_less) )
    {
      for ( unsigned long int j = 0; j < exponent; j++ )
      {
        mpz_pow_ui ( rop->get_mpz_t (), module->get_mpz_t (), 2 );
        *module = *rop % *number;

        if ( *module == 1 )
        {
          probablyPrime = false;
          break; // we*re done, no prime!
        }
        else if ( *module = *number_less )
        {
          break; // do outer loop again!
        }
        else if ( j == ( exponent - 1 ) )
        {
          probablyPrime = false;
          break; // we're also done
        }
      }

      // to cancle the outer loop if the inner got the solution = no prime!
      if ( !probablyPrime )
        break;
    }
  }

  // cleaning up
  delete rop;
  delete rand;
  delete number_less;
  delete base_2;
  delete base_a;
  delete factor;

  return probablyPrime;
}
