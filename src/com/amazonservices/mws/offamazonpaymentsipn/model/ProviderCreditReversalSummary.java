/*******************************************************************************
 *  Copyright 2013 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at:
 *  http://aws.amazon.com/apache2.0
 *  This file is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR
 *  CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the
 *  specific language governing permissions and limitations under the
 *  License.
 * *****************************************************************************
 */
package com.amazonservices.mws.offamazonpaymentsipn.model;

import com.amazonservices.mws.offamazonpayments.OffAmazonPaymentsServiceException;
import com.amazonservices.mws.offamazonpayments.common.JSONFragmentBuilder;
import com.amazonservices.mws.offamazonpayments.common.ReflectionFragmentBuilder;
import com.amazonservices.mws.offamazonpayments.common.XmlFragmentBuilder;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>
 * Java class for ProviderCreditReversalSummary complex type.
 * 
 * <p>
 * The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="ProviderCreditReversalSummary">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="ProviderSellerId" type="{http://mws.amazonservices.com/schema/OffAmazonPayments/2013-01-01}NonEmptyString"/>
 *         &lt;element name="ProviderCreditReversalId" type="{http://mws.amazonservices.com/schema/OffAmazonPayments/2013-01-01}NonEmptyString"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "ProviderCreditReversalSummary", propOrder = { "providerSellerId", "providerCreditReversalId" })
public class ProviderCreditReversalSummary {

    @XmlElement(name = "ProviderSellerId", required = true)
    protected String providerSellerId;
    @XmlElement(name = "ProviderCreditReversalId", required = true)
    protected String providerCreditReversalId;

    public ProviderCreditReversalSummary() {
        super();
    }

    public ProviderCreditReversalSummary(final String providerSellerId, final String providerCreditReversalId) {
        this.providerSellerId = providerSellerId;
        this.providerCreditReversalId = providerCreditReversalId;
    }

    public String getProviderSellerId() {
        return providerSellerId;
    }

    public void setProviderSellerId(String value) {
        this.providerSellerId = value;
    }

    public boolean isSetProviderSellerId() {
        return (this.providerSellerId != null);
    }

    public String getProviderCreditReversalId() {
        return providerCreditReversalId;
    }

    public void setProviderCreditReversalId(String value) {
        this.providerCreditReversalId = value;
    }

    public boolean isSetProviderCreditReversalId() {
        return (this.providerCreditReversalId != null);
    }

    public ProviderCreditReversalSummary withProviderSellerId(String value) {
        setProviderSellerId(value);
        return this;
    }

    public ProviderCreditReversalSummary withProviderCreditReversalId(String value) {
        setProviderCreditReversalId(value);
        return this;
    }

    /**
     *
     * XML fragment representation of this object
     *
     * @return XML fragment for this object. Name for outer
     * tag expected to be set by calling method. This fragment
     * returns inner properties representation only
     */
    @Deprecated
    public String toXMLFragment() throws OffAmazonPaymentsServiceException {
        return new ReflectionFragmentBuilder(this, new XmlFragmentBuilder()).build();
    }

    /**
     *
     * JSON fragment representation of this object
     *
     * @return JSON fragment for this object. Name for outer
     * object expected to be set by calling method. This fragment
     * returns inner properties representation only
     *
     */
    @Deprecated
    public String toJSONFragment() throws OffAmazonPaymentsServiceException {
        return new ReflectionFragmentBuilder(this, new JSONFragmentBuilder()).build();
    }
}