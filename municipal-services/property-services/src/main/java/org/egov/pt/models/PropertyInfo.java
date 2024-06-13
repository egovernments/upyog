package org.egov.pt.models;

import java.util.List;

import javax.validation.Valid;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.egov.pt.models.enums.Status;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * This is lightweight property object that can be used as reference by
 * definitions needing property linking. Actual Property Object extends this to
 * include more elaborate attributes of the property.
 */

@ToString
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PropertyInfo {

	@JsonProperty("id")
	private String id;

	@JsonProperty("propertyId")
	private String propertyId;

	@JsonProperty("surveyId")
	private String surveyId;

	@JsonProperty("linkedProperties")
	@Valid
	private List<String> linkedProperties;

	@JsonProperty("tenantId")
	private String tenantId;

	@JsonProperty("accountId")
	private String accountId;

	@JsonProperty("oldPropertyId")
	private String oldPropertyId;

	@JsonProperty("status")
	private Status status;

	@JsonProperty("address")
	private Address address;
}
