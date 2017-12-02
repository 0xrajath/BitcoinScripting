package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;
import java.net.UnknownHostException;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class LinearEquationTransaction extends ScriptTransaction {
    public LinearEquationTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend by two numbers x and y such that x+y=first 4 digits of your suid and |x-y|=last 4 digits of your suid (perhaps +1)
    	
    		ScriptBuilder builder = new ScriptBuilder();
    		builder.op(OP_2DUP); //Duplicates Top 2 elements of stack so that there are two pairs of x,y in the stack
    		builder.op(OP_ADD); //Adds top 2 elements of stack i.e x and y
    		builder.data(encode(BigInteger.valueOf(1661))); //Adding x+y value i.e first half of N number to stack
    		builder.op(OP_EQUALVERIFY); //Comparing if x+y and firstHalf of N number are equal and doing verify as well
    		builder.op(OP_SUB); //Computes x-y
    		builder.data(encode(BigInteger.valueOf(9151))); //Adding x-y value i.e secondHalf of N number to stack
    		builder.op(OP_EQUAL); //Comparing if x-y and secondHalf of N number are equal    		
    		
        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedScript) {
        // TODO: Create a spending script		
		
		ScriptBuilder builder = new ScriptBuilder();
	    builder.data(encode(BigInteger.valueOf(5406))); //Adding x to stack
	    builder.data(encode(BigInteger.valueOf(-3745))); //Adding y to stack
	    return builder.build();
    }

    private byte[] encode(BigInteger bigInteger) {
        return Utils.reverseBytes(Utils.encodeMPI(bigInteger, false));
    }
}
