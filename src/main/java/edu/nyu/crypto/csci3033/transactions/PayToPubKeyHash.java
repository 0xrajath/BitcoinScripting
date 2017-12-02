package edu.nyu.crypto.csci3033.transactions;

import org.bitcoinj.core.AddressFormatException;
import org.bitcoinj.core.Base58;
import org.bitcoinj.core.DumpedPrivateKey;
import org.bitcoinj.core.ECKey;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.core.Transaction;
import org.bitcoinj.crypto.DeterministicKey;
import org.bitcoinj.crypto.TransactionSignature;
import org.bitcoinj.params.MainNetParams;
import org.bitcoinj.script.Script;
import org.bitcoinj.script.ScriptBuilder;

import java.io.File;

import static org.bitcoinj.script.ScriptOpCodes.*;

/**
 * Created by bbuenz on 24.09.15.
 */
public class PayToPubKeyHash extends ScriptTransaction {
	//Testnet
	//private String vanityPrivatekey = "93LT6okfrJXQdaA2AFQPG3YAdHNgtbAmRRXYqW51sK2d9XHNGCL";
	//private String vanityAddress = "mgvvfcEE1GaWs4r4eqiKPbVpsQDD1vBjLm";
	
	//Mainnet
	private String vanityPrivatekey = "5KbTfaNWjsSojQc4LEghNTRDDF8MENdAaC97Bxd2DdVPiZthNo8";
	private String vanityAddress = "1aLexKLU8Y2ZuKigCGVmpw5W1WALHSKet";
	private ECKey key;
	
    public PayToPubKeyHash(NetworkParameters parameters, File file, String password) throws AddressFormatException {
        super(parameters, file, password);
        DumpedPrivateKey dumpedPrivateKey = new DumpedPrivateKey(parameters, vanityPrivatekey);
        key = dumpedPrivateKey.getKey();
    }

    @Override
    public Script createInputScript() {
        // TODO: Create a P2PKH script
        // TODO: be sure to test this script on the mainnet using a vanity address
    		ScriptBuilder builder = new ScriptBuilder();
    		builder.op(OP_DUP);
    		builder.op(OP_HASH160);
        builder.data(key.getPubKeyHash());
        builder.op(OP_EQUALVERIFY);
        builder.op(OP_CHECKSIG);
        return builder.build();
    }

    @Override
    public Script createRedemptionScript(Transaction unsignedTransaction) {
        // TODO: Redeem the P2PKH transaction
    		TransactionSignature txSig = sign(unsignedTransaction, key);

        ScriptBuilder builder = new ScriptBuilder();
        builder.data(txSig.encodeToBitcoin());
        builder.data(key.getPubKey());
                
        return builder.build();
    }
}
