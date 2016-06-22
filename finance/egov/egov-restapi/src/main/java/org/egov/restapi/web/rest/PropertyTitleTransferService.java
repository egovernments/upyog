/*
 * eGov suite of products aim to improve the internal efficiency,transparency,
 *    accountability and the service delivery of the government  organizations.
 *
 *     Copyright (C) <2015>  eGovernments Foundation
 *
 *     The updated version of eGov suite of products as by eGovernments Foundation
 *     is available at http://www.egovernments.org
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program. If not, see http://www.gnu.org/licenses/ or
 *     http://www.gnu.org/licenses/gpl.html .
 *
 *     In addition to the terms of the GPL license to be adhered to in using this
 *     program, the following additional terms are to be complied with:
 *
 *         1) All versions of this program, verbatim or modified must carry this
 *            Legal Notice.
 *
 *         2) Any misrepresentation of the origin of the material is prohibited. It
 *            is required that all modified versions of this material be marked in
 *            reasonable ways as different from the original version.
 *
 *         3) This license does not grant any rights to any user of the program
 *            with regards to rights under trademark law for use of the trade names
 *            or trademarks of eGovernments Foundation.
 *
 *   In case of any queries, you can reach eGovernments Foundation at contact@egovernments.org.
 */
package org.egov.restapi.web.rest;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.MediaType;

import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonParseException;
import org.codehaus.jackson.annotate.JsonAutoDetect.Visibility;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.SerializationConfig;
import org.egov.dcb.bean.ChequePayment;
import org.egov.infra.config.core.ApplicationThreadLocals;
import org.egov.infra.validation.exception.ValidationError;
import org.egov.infra.validation.exception.ValidationException;
import org.egov.ptis.domain.model.ErrorDetails;
import org.egov.ptis.domain.model.NewPropertyDetails;
import org.egov.ptis.domain.model.OwnerDetails;
import org.egov.ptis.domain.model.PayPropertyTaxDetails;
import org.egov.ptis.domain.model.ReceiptDetails;
import org.egov.ptis.domain.model.RestAssessmentDetails;
import org.egov.ptis.domain.service.property.PropertyExternalService;
import org.egov.ptis.domain.service.transfer.PropertyTransferService;
import org.egov.restapi.model.AssessmentRequest;
import org.egov.restapi.model.OwnerInformation;
import org.egov.restapi.model.PropertyTransferDetails;
import org.egov.restapi.util.JsonConvertor;
import org.egov.restapi.util.ValidationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class PropertyTitleTransferService {

	@Autowired
	private ValidationUtil validationUtil;
	
	@Autowired
    private PropertyExternalService propertyExternalService;
	
	@Autowired
	private PropertyTransferService transferOwnerService;
	 
	/**
	 * Initiates property transfer
	 * @param titleTransferDetails
	 * @return
	 * @throws JsonGenerationException
	 * @throws JsonMappingException
	 * @throws IOException
	 * @throws ParseException
	 */
	@RequestMapping(value = "/property/titletransfer", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public String transferProperty(@RequestBody String titleTransferDetails)
            throws JsonGenerationException, JsonMappingException, IOException, ParseException {
		String responseJson = new String();
		ApplicationThreadLocals.setUserId(2L);
		
		PropertyTransferDetails propertyTransferDetails = (PropertyTransferDetails) getObjectFromJSONRequest(
				titleTransferDetails, PropertyTransferDetails.class);
		
		ErrorDetails errorDetails = validationUtil.validatePropertyTransferRequest(propertyTransferDetails);
		if (errorDetails != null) {
            responseJson = JsonConvertor.convert(errorDetails);
        } else {
        	String assessmentNo = propertyTransferDetails.getAssessmentNo();
        	String mutationReasonCode = propertyTransferDetails.getMutationReasonCode();
        	String saleDetails = propertyTransferDetails.getSaleDetails();
        	String deedNo = propertyTransferDetails.getDeedNo();
        	String deedDate = propertyTransferDetails.getDeedDate();
        	List<OwnerDetails> ownerDetailsList = getOwnerDetails(propertyTransferDetails.getOwnerDetails());

        	NewPropertyDetails newPropertyDetails = transferOwnerService.createPropertyMutation(assessmentNo, mutationReasonCode,
        			saleDetails, deedNo, deedDate, ownerDetailsList);
        	
        	responseJson = JsonConvertor.convert(newPropertyDetails);
        }
		
		return responseJson;
	}
	
	/**
	 * Prepares list of OwnerDetails from OwnerInformation
	 * @param ownerInfoList
	 * @return
	 */
	private List<OwnerDetails> getOwnerDetails(List<OwnerInformation> ownerInfoList){
		List<OwnerDetails> ownerDetailsList = new ArrayList<OwnerDetails>();
		OwnerDetails ownerDetails ;
		for(OwnerInformation ownerInfo : ownerInfoList){
			ownerDetails = new OwnerDetails();
			ownerDetails.setAadhaarNo(ownerInfo.getAadhaarNo());
			ownerDetails.setSalutationCode(ownerInfo.getSalutationCode());
			ownerDetails.setName(ownerInfo.getName());
			ownerDetails.setGender(ownerInfo.getGender());
			ownerDetails.setMobileNumber(ownerInfo.getMobileNumber());
			ownerDetails.setEmailId(ownerInfo.getEmailId());
			ownerDetails.setGuardianRelation(ownerInfo.getGuardianRelation());
			ownerDetails.setGuardian(ownerInfo.getGuardian());
			ownerDetailsList.add(ownerDetails);
		}
		return ownerDetailsList;
	}
	
	 /**
     * This method loads the assessment details.
     * 
     * @param applicationNo - Mutation Application Number
     * @param assessmentNumber - assessment number i.e. property id
     * @return
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @RequestMapping(value = "/property/assessmentdetails", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public String fetchAssessmentDetails(@RequestBody String assessmentRequest)
            throws JsonGenerationException, JsonMappingException, IOException {
        AssessmentRequest assessmentReq = (AssessmentRequest) getObjectFromJSONRequest(assessmentRequest,
                AssessmentRequest.class);
        String responseJson = new String();
        
        ErrorDetails errorDetails = validationUtil.validateAssessmentDetailsRequest(assessmentReq);
        if (errorDetails != null) {
            responseJson = getJSONResponse(errorDetails);
        } else {
	        RestAssessmentDetails assessmentDetails = propertyExternalService
	                .loadAssessmentDetails(assessmentReq.getApplicationNo(),assessmentReq.getAssessmentNo());
	        responseJson = getJSONResponse(assessmentDetails);
        }
        return responseJson;
    }
    
    /**
     * This method is used to pay the mutation fee
     * 
     * @param payPropertyTaxDetails - JSON request string
     * @param request - HttpServletRequest
     * @return responseJson - server response in JSON format
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    @RequestMapping(value = "/property/paymutationfee", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON, produces = MediaType.APPLICATION_JSON)
    public String payMutationFee(@RequestBody String payPropertyTaxDetails, final HttpServletRequest request, String source)
            throws JsonGenerationException, JsonMappingException, IOException {
        String responseJson;
        try {
            responseJson = new String();
            PayPropertyTaxDetails payPropTaxDetails = (PayPropertyTaxDetails) getObjectFromJSONRequest(
                    payPropertyTaxDetails, PayPropertyTaxDetails.class);

            ErrorDetails errorDetails = validationUtil.validatePaymentDetails(payPropTaxDetails,true);
            if (null != errorDetails) {
                responseJson = getJSONResponse(errorDetails);
            } else {
            	if(StringUtils.isNotBlank(source))
            		payPropTaxDetails.setSource(source);
            	else
	                payPropTaxDetails.setSource(request.getSession().getAttribute("source") != null ? request.getSession()
	                        .getAttribute("source").toString()
	                        : "");
                ReceiptDetails receiptDetails = propertyExternalService.payMutationFee(payPropTaxDetails);
                responseJson = getJSONResponse(receiptDetails);
            }
        } catch (ValidationException e) {
            e.printStackTrace();
            List<ErrorDetails> errorList = new ArrayList<ErrorDetails>(0);

            List<ValidationError> errors = e.getErrors();
            for (ValidationError ve : errors)
            {
                ErrorDetails er = new ErrorDetails();
                er.setErrorCode(ve.getKey());
                er.setErrorMessage(ve.getMessage());
                errorList.add(er);
            }
            responseJson = JsonConvertor.convert(errorList);
        } catch (Exception e) {
            e.printStackTrace();
            List<ErrorDetails> errorList = new ArrayList<ErrorDetails>(0);
            ErrorDetails er = new ErrorDetails();
            er.setErrorCode(e.getMessage());
            er.setErrorMessage(e.getMessage());
            errorList.add(er);
            responseJson = JsonConvertor.convert(errorList);
        }
        return responseJson;
    }
    
	/**
     * This method is used to get POJO object from JSON request.
     * 
     * @param jsonString - request JSON string
     * @return
     * @throws JsonParseException
     * @throws JsonMappingException
     * @throws IOException
     */
    private Object getObjectFromJSONRequest(String jsonString, Class cls)
            throws JsonParseException, JsonMappingException, IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
        mapper.configure(SerializationConfig.Feature.AUTO_DETECT_FIELDS, true);
        mapper.setDateFormat(ChequePayment.CHEQUE_DATE_FORMAT);
        return mapper.readValue(jsonString, cls);
    }
    
    /**
     * This method is used to prepare jSON response.
     * 
     * @param obj - a POJO object
     * @return jsonResponse - JSON response string
     * @throws JsonGenerationException
     * @throws JsonMappingException
     * @throws IOException
     */
    private String getJSONResponse(Object obj) throws JsonGenerationException, JsonMappingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.setVisibility(JsonMethod.FIELD, Visibility.ANY);
        String jsonResponse = objectMapper.writeValueAsString(obj);
        return jsonResponse;
    }
    
}
