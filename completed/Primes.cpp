#include <iostream>
#include <thread>
#include <vector>
#include <atomic>
#include <chrono>
#include <array>
#include <cmath>
#include <algorithm>
#include <typeinfo>
#include <type_traits>
#define UPPER_BOUND 100000000
#define NUM_THREADS 8

// g++ –std=c++17 thread_playground.cpp -lpthread (type '–std=c++17' out manually. Don't copy
// & paste because the copy & pasted hyphen isn't recognized)


using namespace std;
using namespace std::chrono;


void SieveEmForGood(int startInd, int endInd, bool primes[])
{
    int res = (int) sqrt(endInd);
    for (int i = startInd; i < res; i+=2)
    {
        if (primes[i])
        {
            // sieve to the upper bound
            for (int j = i * i; j < endInd; j += i)
            {
                // cross it out!
                // The given index is not prime!
                primes[j] = false;
            }
        }
    }
}

void dummy(vector<int>& test)
{
    test[0] = 1;
}

void dummystr(string& test)
{
    test = "bye";
}

void intArray(int array[])
{
    array[0] = 1;
}

void IntPtr(int* array)
{
    array[0] = 1;
}

template<typename T>
void print(T msg)
{
    std::cout << msg << std::endl;
}

int main(int argc, char const *argv[])
{
    #pragma region old code

    // vector<int> test { 1,2,3,4,5,6,7,8,9,10 };

    // for (vector<int>::iterator it = test.end() - 1; it != test.begin() - 1; --it)
    // {
    //     print(*it);
    // }

    // int array[1] = { 0 };
    // std::cout << array[0] << std::endl;
    // IntPtr(array);
    // std::cout << array[0] << std::endl;

    // int* intPtr = new int[1];
    // std::cout << intPtr[0] << std::endl;
    // IntPtr(intPtr);
    // std::cout << intPtr[0] << std::endl;

    // delete [] intPtr;

    // std::array<bool, 5> liraryArray;
    // bool myArray[5];
    // std::cout << is_same<bool[5], array<bool, 5>>::value << std::endl;
    // thread worker(my_num, 5);
    // cin.get();
    // isDone = true;
    // worker.join();
    // cin.get();

    // std::hash<std::thread::id>{}(std::this_thread::get_id())
    // std::cout << "finished in main: " << hash<thread::id>()(this_thread::get_id()) << std::endl;

    // vector<int> test(3);

    // std::cout << test[0] << std::endl;

    // dummy(test);

    // std::cout << test[0] << std::endl;

    // string hi = "hi";

    // std::cout << hi << std::endl;

    // dummystr(hi);

    // std::cout << hi << std::endl;

#pragma endregion


    // makes sure bools use true/false instead of 0s and 1s
    std::cout << boolalpha << std::endl;

    // starting points for the sieve
    int primeNums[] = {3, 5, 7, 11, 13, 17, 31, 41};

    // bool array for whether an index is prime or not
    // assume true until we sieve it to be false
    // in the sieve worker
    // vector<bool> primes(UPPER_BOUND + 1);
    // std::fill(primes.begin(), primes.end(), true);
    // primes[0] = primes[1] = false;

    // why does a boolean vector not do the job like this a bool array does?
    bool *primes = new bool[UPPER_BOUND + 1];

    // Set the initial values of the primes array
    memset(primes, true, UPPER_BOUND + 1);
    primes[0] = primes[1] = false;
    // for (int i = 0; i < 10; ++i)
    // {
    //     print(primes[i]);
    // }


    // create 8 threads
    vector<thread> threadArray;

    auto startTime = high_resolution_clock::now();

    // run each thread at the right location
    for (int i = 0; i < NUM_THREADS; i++)
    {
        // give each of the 8 threads a starting point
        threadArray.push_back(thread(SieveEmForGood, primeNums[i], UPPER_BOUND, primes));
    }

    // join each thread
    for_each(threadArray.begin(), threadArray.end(), mem_fn(&thread::join));

    auto endTime = high_resolution_clock::now();
    auto duration = duration_cast<milliseconds>(endTime - startTime);

    std::cout << duration.count() << " milliseconds" << std::endl;

    // have a list of all the prime numbers between 2 and 100,000,000, inclusive
    // from there, we can get a count, sum, and the 10 largest prime numbers
    vector<int> results;
    results.push_back(2L);
    long sum = 2L;

    // we skip every even number because they're definitely not prime
    for (int i = 3; i < UPPER_BOUND; i += 2)
    {
        if (primes[i])
        {
            results.push_back(i);
            sum += i;
        }
    }

    int len = results.size();

    std::cout << "number of prime numbers under 10^8: " << len << std::endl;
    std::cout << "total sum of all prime numbers under 10^8: " << sum << std::endl;

    // second constructor argument is exclusive
    vector<int> subVector (results.end() - 10, results.end());

    std::cout << std::endl;
    print("ten largest primes:");
    for(auto n : subVector)
    {
        print(n);
    }

    std::cout << "done" << std::endl;

    // clean up after yourself
    delete [] primes;

    return 0;
}
