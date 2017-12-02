package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.core.Utils;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;
import java.math.BigInteger;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class MultiSigTransaction extends ScriptTransaction {
	
	private DeterministicKey bankKey;
	private DeterministicKey customer1Key;
	private DeterministicKey customer2Key;
	private DeterministicKey customer3Key;
	
    public MultiSigTransaction(NetworkParameters parameters, File file, String password) {
        super(parameters, file, password);
        //Getting individual keys for all parties involved
        bankKey = getWallet().freshReceiveKey();
        customer1Key = getWallet().freshReceiveKey();
        customer2Key = getWallet().freshReceiveKey();
        customer3Key = getWallet().freshReceiveKey();
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a script that can be spend using signatures from the bank and one of the customers
    	
    		ScriptBuilder builder = new ScriptBuilder();
    		//Duplicating the Top 3 elements twice
    		builder.op(OP_3DUP);
    		builder.op(OP_3DUP);
    		
    		//Moving Top 6 elements in Stack to Alternate Stack
    		builder.op(OP_TOALTSTACK);
        builder.op(OP_TOALTSTACK);
        builder.op(OP_TOALTSTACK);
        builder.op(OP_TOALTSTACK);
        builder.op(OP_TOALTSTACK);
        builder.op(OP_TOALTSTACK);
        
        //Checking Multisig between Bank and Customer1
        builder.op(OP_2); //Adding for 2 Signatures in MultiSig
        builder.data(bankKey.getPubKey());
        builder.data(customer1Key.getPubKey());
        builder.op(OP_2); //Adding for total 2 Public Keys in MultiSig
        builder.op(OP_CHECKMULTISIG);
    		
        //Moving Top 3 elements from Alternate Stack to Main Stack
        builder.op(OP_FROMALTSTACK);
        builder.op(OP_FROMALTSTACK);
        builder.op(OP_FROMALTSTACK);
        
        //Checking Multisig between Bank and Customer2
        builder.op(OP_2); //Adding for 2 Signatures in MultiSig
        builder.data(bankKey.getPubKey());
        builder.data(customer2Key.getPubKey());
        builder.op(OP_2); //Adding for total 2 Public Keys in MultiSig
        builder.op(OP_CHECKMULTISIG);
        
        builder.op(OP_BOOLOR); //Doing a Boolean OR between the first two evaluated MultiSigs
        
        //Moving Remaining 3 elements from Alternate Stack to Main Stack
        builder.op(OP_FROMALTSTACK);
        builder.op(OP_FROMALTSTACK);
        builder.op(OP_FROMALTSTACK);
        
        //Checking Multisig between Bank and Customer3
        builder.op(OP_2); //Adding for 2 Signatures in MultiSig
        builder.data(bankKey.getPubKey());
        builder.data(customer3Key.getPubKey());
        builder.op(OP_2); //Adding for total 2 Public Keys in MultiSig
        builder.op(OP_CHECKMULTISIG);
        
        builder.op(OP_BOOLOR); //Doing a Boolean OR between the earlier evaluated MultiSig and new evaluated MultiSig
        
        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // Please be aware of the CHECK_MULTISIG bug!
        // TODO: Create a spending script
    	
    		TransactionSignature txSigBank = sign(unsignedTransaction, bankKey);
    		TransactionSignature txSigCustomer1 = sign(unsignedTransaction, customer1Key);
    		//TransactionSignature txSigCustomer2 = sign(unsignedTransaction, customer2Key);
    		//TransactionSignature txSigCustomer3 = sign(unsignedTransaction, customer3Key);
    	
    		ScriptBuilder builder = new ScriptBuilder();
		//Bank and Customer1 signing
    		builder.op(OP_5); //Adding random element to pop out for multisig bug
		builder.data(txSigBank.encodeToBitcoin()); 
		builder.data(txSigCustomer1.encodeToBitcoin());
		
		//Bank and Customer2 signing
//		builder.op(OP_5); //Adding random element to pop out for multisig bug
//		builder.data(txSigBank.encodeToBitcoin()); 
//		builder.data(txSigCustomer2.encodeToBitcoin());
		
		//Bank and Customer3 signing
//		builder.op(OP_5); //Adding random element to pop out for multisig bug
//		builder.data(txSigBank.encodeToBitcoin()); 
//		builder.data(txSigCustomer3.encodeToBitcoin()); 		
		
		return builder.build();
    }
    
    private byte[] encode(BigInteger bigInteger) {
        return Utils.reverseBytes(Utils.encodeMPI(bigInteger, false));
    }
    
}
