package test.benchmark;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import benchmark.Benchmark;
public class BenchmarkTest {
	
	@Test
	public void TestCalculateScore(){
		File testBed = new File("resources");
		Double score = Benchmark.calculateScore(testBed);
		assertTrue(score!=null);
		assertTrue(score>0.0);
		
	}

}
