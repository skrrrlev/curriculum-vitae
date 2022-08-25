/*
Author: Ditlev Frickmann

I've been using C# and .NET to build automated tests of SDKs.
*/

using System;
using Xunit;

namespace CurriculumVitae
{
    public class Tests
    {
        [Fact]
        public void LoadIIQ()
        {
            try
            {
                //
            }
            catch (Exception e)
            {
                Console.WriteLine("Non-SDK error: {0}", e.Message);
                throw;
            }
        }
    }
}