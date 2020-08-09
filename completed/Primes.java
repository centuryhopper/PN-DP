import java.util.ArrayList;
import java.util.Arrays;

public class Primes
{
    private static void CheckThreadCompletion(Thread [] threads)
    {
        boolean allThreadsAreDone = false;
        while (!allThreadsAreDone)
        {
            for (Thread t : threads)
            {
                if (t.isAlive())
                {
                    allThreadsAreDone = false;
                    break;
                }
                allThreadsAreDone = true;
            }
            // break will take us here
        }
    }

    private static void TheFunctionThatDoesEverything()
    {
        Thread[] sieveThreads = new Thread[8];
        UseSieves[] cPrimes = new UseSieves[8];
        int upperBound = 100000000;
        boolean[] primes = new boolean[upperBound + 1];
        int[] primeNums = new int[] {3, 5, 7, 11, 13, 17, 31, 41};
        Arrays.fill(primes, true);
        primes[0] = primes[1] = false;

        // create the threads
        CreateThreadWorkers(cPrimes, upperBound, primes, primeNums);

        // run the threads
        RunDemThreads(sieveThreads, cPrimes);

        // let all the threads process then get all the primes afterwards
        CheckThreadCompletion(sieveThreads);

        // write code here to loop thru 10^8 to get all primes
        GetFinalizedContents(upperBound, primes);
    }

    private static void GetFinalizedContents(int upperBound, boolean[] primes)
    {
        ArrayList<Integer> a = new ArrayList<>();
        a.add(2);
        Long sum = 2L;
        for (int i = 3; i < upperBound; i += 2)
        {
            if (primes[i])
            {
                a.add(i);
                sum += i;
            }
        }

        int len = a.size();
        System.out.println("total sum: "+sum);
        System.out.println("number of primes: "+len);
        System.out.println("ten largest primes: "+a.subList(len - 10, len));
    }

    // Run each thread one at a time
    private static void RunDemThreads(Thread[] sieveThreads, UseSieves[] cPrimes)
    {
        for (int i = 0; i < sieveThreads.length; i++)
        {
            sieveThreads[i] = new Thread(cPrimes[i]);
            sieveThreads[i].start();
            try {
                sieveThreads[i].join();
            } catch (Exception e) {
                System.out.println("exception caught");
            }
        }
    }

    // This method sets the starting points for each thread
    private static void CreateThreadWorkers(UseSieves[] cPrimes, int upperBound, boolean[] primes, int[] primeNums)
    {
        for (int i = 0; i < cPrimes.length; i++)
        {
            // create 8 calculateprimes objects each with unique start and ends
            cPrimes[i] = new UseSieves(primeNums[i], upperBound, primes);
        }
    }

    public static void main(String[] args)
    {
        long st = System.nanoTime();
        TheFunctionThatDoesEverything();
        long end = System.nanoTime();

        System.out.println("Total runtime (in nanoseconds): " + (end - st));
    }
}

class UseSieves implements Runnable
{
    private int startInd, endInd;
    private boolean[] primes;
    
    public UseSieves(int startInd, int endInd, boolean [] primes)
    {
        this.startInd = startInd; this.endInd = endInd;
        this.primes = primes;
    }

    public synchronized void SieveEmForGood()
    {
        // caching should increase efficiency rather than repeatedly
        // making the function call
        int res = (int) Math.sqrt(endInd);
        for (int i = startInd; i < res; i += 2)
        {
            if (primes[i])
            {
                for (int j = i*i; j < endInd; j += i)
                {
                    primes[j] = false;
                }
            }
        }
    }

    @Override
    public void run()
    {
        // do sieves here.
        // each thread will sieve the range of numbers in multiples of
        // its starting point
        SieveEmForGood();
    }
}
