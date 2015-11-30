package org.fiteagle.adapters.epc.model;

import info.openmultinet.ontology.vocabulary.Epc;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.fiteagle.adapters.epc.CommonMethods;

import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;

public class PDNGateway {

	private static final Logger LOGGER = Logger.getLogger(PDNGateway.class
			.toString());

	private String name;
	private int delayCode;
	private int packetlossCode;
	private int rateCode;
	private String ip;
	private String stopCommand;
	private String startCommand;

	public PDNGateway(String ip, String startCommand, String stopCommand) {
		this.name = null;
		this.delayCode = -1;
		this.rateCode = -1;
		this.packetlossCode = -1;
		this.ip = ip;
		this.startCommand = startCommand;
		this.stopCommand = stopCommand;
	}

	/**
	 * Run the script to stop a running instance of an EPC resource
	 */
	public String stopInstance() {
		LOGGER.log(Level.INFO, "stopInstance");
		String output = CommonMethods.executeCommand(stopCommand);
		return output;
	}

	public String startInstance() {
		LOGGER.log(Level.INFO, "startInstance");
		String output = CommonMethods.executeCommand(startCommand);
		return output;
	}

	/**
	 * Runs the script to update rate, delay, and packtloss of current EPC
	 * resource
	 * 
	 * @param rateCode
	 * @param delayCode
	 * @param packetlossCode
	 */
	public String updateRateDelayPacktloss(int rateCode, int delayCode,
			int packetlossCode) {

		LOGGER.log(Level.INFO, "updateRateDelayPacktloss");

		String rateCodeString = Integer.toString(rateCode);
		String delayCodeString = Integer.toString(delayCode);
		String packetlossCodeString = Integer.toString(packetlossCode);

		String command = "ssh " + ip + " " + rateCodeString + " "
				+ delayCodeString + " " + packetlossCodeString;

		LOGGER.log(Level.INFO, "Try to execute command: " + command);
		String output = CommonMethods.executeCommand(command);
		LOGGER.log(Level.INFO, "updateRateDelayPacktloss test output: "
				+ output);

		return output;
	}

	public String updateRateDelayPacktloss() {

		String output = updateRateDelayPacktloss(this.getRateCode(),
				this.getDelayCode(), this.getPacketlossCode());
		return output;
	}

	/**
	 * Update the values of the PDN gateway, according to the update model
	 * received
	 * 
	 * @param pgwResource
	 */
	public void updateInstance(Resource pgwResource) {

		boolean isRateLossDelayChanged = false;

		if (pgwResource.hasProperty(RDFS.label)) {
			String name = pgwResource.getProperty(RDFS.label).getObject()
					.asLiteral().getString();

			this.setName(name);
		}

		if (pgwResource.hasProperty(Epc.delayCode)) {
			int delay = pgwResource.getProperty(Epc.delayCode).getObject()
					.asLiteral().getInt();

			if (delay != this.getDelayCode()) {
				this.setDelayCode(delay);
				isRateLossDelayChanged = true;
			}
		}

		if (pgwResource.hasProperty(Epc.rateCode)) {
			int rate = pgwResource.getProperty(Epc.rateCode).getObject()
					.asLiteral().getInt();

			if (rate != this.getRateCode()) {
				this.setRateCode(rate);
				isRateLossDelayChanged = true;
			}
		}

		if (pgwResource.hasProperty(Epc.packetlossCode)) {
			int packetLoss = pgwResource.getProperty(Epc.packetlossCode)
					.getObject().asLiteral().getInt();

			if (packetLoss != this.getPacketlossCode()) {
				this.setPacketlossCode(packetLoss);
				isRateLossDelayChanged = true;
			}
		}

		if (isRateLossDelayChanged) {
			this.updateRateDelayPacktloss();
		}

	}

	/**
	 * Output the values of this PDN gateway as an OMN model
	 * 
	 * @param pgwResource
	 */
	public void parseToModel(Resource pgwResource) {

		pgwResource.addProperty(RDF.type,
				info.openmultinet.ontology.vocabulary.Epc.PDNGateway);

		if (this.getName() != null) {
			pgwResource.addLiteral(RDFS.label, this.getName());
		}

		if (this.getRateCode() != -1) {
			BigInteger rate = BigInteger.valueOf(this.getRateCode());
			pgwResource.addLiteral(
					info.openmultinet.ontology.vocabulary.Epc.rateCode, rate);
		}

		if (this.getDelayCode() != -1) {
			BigInteger delay = BigInteger.valueOf(this.getDelayCode());
			pgwResource.addLiteral(
					info.openmultinet.ontology.vocabulary.Epc.delayCode, delay);
		}

		if (this.getPacketlossCode() != -1) {
			BigInteger loss = BigInteger.valueOf(this.getPacketlossCode());
			pgwResource.addLiteral(
					info.openmultinet.ontology.vocabulary.Epc.packetlossCode,
					loss);
		}
	}

	/**
	 * Getters and setters
	 */
	public int getDelayCode() {
		return this.delayCode;
	}

	public void setDelayCode(int delayCode) {
		this.delayCode = delayCode;
	}

	public int getPacketlossCode() {
		return this.packetlossCode;
	}

	public void setPacketlossCode(int packetlossCode) {
		this.packetlossCode = packetlossCode;
	}

	public int getRateCode() {
		return this.rateCode;
	}

	public void setRateCode(int rateCode) {
		this.rateCode = rateCode;
	}

	public String getName() {
		return this.name;
	}

	public void setName(String name) {
		this.name = name;
	}
}