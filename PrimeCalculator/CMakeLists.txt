cmake_minimum_required(VERSION 3.2)
project(LSW_Aufgaben)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11")

set(SOURCE_FILES main.cpp)

add_executable(LSW_Aufgaben ${SOURCE_FILES} "4096-Bit Prime/PrimeCalculator.cpp" "4096-Bit Prime/PrimeCalculator.h")
target_link_libraries(LSW_Aufgaben gmp)