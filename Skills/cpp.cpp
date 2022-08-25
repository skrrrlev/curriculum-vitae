/*
Author: Ditlev Frickmann

I've been using C++, CMake and CTest to build automated tests of SDKs.
*/
#include <iostream>
#include <vector>

using std::cout;
using std::endl;

int main(int argc, const char** argv)
{  
    
    try 
   {
        /* Test */

        return 0;
    }
    catch(const std::runtime_error& re)
    {
        // speciffic handling for runtime_error
        std::cerr << "Runtime error: " << re.what() << std::endl;
    }
    catch(const std::exception& ex)
    {
        // speciffic handling for all exceptions extending std::exception, except
        // std::runtime_error which is handled explicitly
        std::cerr << "Error occurred: " << ex.what() << std::endl;
    }
    catch(...)
    {
        // catch any other errors (that we have no information about)
        std::cerr << "Unknown failure occurred. Possible memory corruption" << std::endl;
    }
    return 1;
}