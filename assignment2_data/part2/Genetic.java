import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;



import org.jgap.InvalidConfigurationException;
import org.jgap.gp.CommandGene;
import org.jgap.gp.GPFitnessFunction;
import org.jgap.gp.GPProblem;
import org.jgap.gp.IGPProgram;
import org.jgap.gp.function.Add;
import org.jgap.gp.function.Divide;
import org.jgap.gp.function.Multiply;
import org.jgap.gp.function.Subtract;
import org.jgap.gp.impl.DeltaGPFitnessEvaluator;
import org.jgap.gp.impl.GPConfiguration;
import org.jgap.gp.impl.GPGenotype;
import org.jgap.gp.terminal.Terminal;
import org.jgap.gp.terminal.Variable;


class GeneticTest extends GPProblem{
    public static Double[] inputs = new Double[20];
    public static Double[] outputs = new Double[20];

    public Variable input;


    public GeneticTest() throws InvalidConfigurationException{
        super(new GPConfiguration());

        GPConfiguration config = getGPConfiguration();

        input = Variable.create(config, "x", CommandGene.DoubleClass);

        config.setGPFitnessEvaluator(new DeltaGPFitnessEvaluator());
        config.setPopulationSize(100);
        config.setFitnessFunction(new GeneticFitnessFunction(inputs, outputs, input));
        config.setStrictProgramCreation(true);
    }
    public static void readFile(File file) {
        try {
            Scanner sc = new Scanner(file);
            String header = sc.nextLine();
            String seperator = sc.nextLine();
            int count = 0;
            while (sc.hasNextLine()) {
                inputs[count] = sc.nextDouble();;
                outputs[count] = sc.nextDouble();;
                count++;
            }
            sc.close();
        } catch (Exception e) {
            System.out.println(e);
        }
    }

    public static void main(String[] args) throws Exception {
        readFile(new File("regression.txt"));

        GPProblem problem = new GeneticTest();

        GPGenotype gp = problem.create();
        gp.setVerboseOutput(true);
        for (int evolutions = 0; gp.getFittestProgram().getFitnessValue() > 0.01; evolutions++) {
            System.out.println("Generation: " + evolutions + " Fitness: " + gp.getFittestProgram().getFitnessValue());
            gp.evolve(1);
        }
        gp.outputSolution(gp.getAllTimeBest());
    }

    @Override
    public GPGenotype create() throws InvalidConfigurationException {
        GPConfiguration config = getGPConfiguration();

        // The return type of the GP program.
        Class[] types = { CommandGene.DoubleClass};

        // Arguments of result-producing chromosome: none
        Class[][] argTypes = {{}};

        // Next, we define the set of available GP commands and terminals to
        // use.
        CommandGene[][] nodeSets = {
            {
                input,
                new Add(config, CommandGene.DoubleClass),
                new Subtract(config, CommandGene.DoubleClass),
                new Multiply(config, CommandGene.DoubleClass),
                new Divide(config, CommandGene.DoubleClass),
                new Terminal(config,CommandGene.DoubleClass, 0.0,1.0)
            }
        };

        GPGenotype result = GPGenotype.randomInitialGenotype(config, types, argTypes,
                nodeSets, 20, true);

        return result;

    }
}



class GeneticFitnessFunction extends GPFitnessFunction{
    private static Object[] NO_ARGS = new Object[0];
    private Double[] _input1;
    private Double[] output;
    private Variable _xVariable;

    
    public GeneticFitnessFunction(Double input1[], Double output[], Variable _xVariable){
        this._input1 = input1;
        this.output = output;
        this._xVariable = _xVariable;
    }

    @Override
    protected double evaluate(final IGPProgram program) {
        double result = 0.0f;
    
        double doubleResult = 0.0;
        for (int i = 0; i < _input1.length; i++) {
            // Set the input values
            _xVariable.set(_input1[i]);
            // Execute the genetically engineered algorithm
            Number value = program.execute_double(0, NO_ARGS);
    
            // The closer doubleResult gets to 0 the better the algorithm.
            doubleResult += Math.abs(value.doubleValue() - output[i]);
        }
    
        result = doubleResult;
    
        return result;
    }



}


