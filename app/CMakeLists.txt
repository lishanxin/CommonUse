
cmake_minimum_required(VERSION 3.4.1)

add_library(
            native-lib
            SHARED
            src/main/cpp/native-lib.cpp )

# Specifies a path to native header files.
include_directories( src/main/cpp/include/ )

find_library( log-lib log )

target_link_libraries( native-lib ${log-lib} )