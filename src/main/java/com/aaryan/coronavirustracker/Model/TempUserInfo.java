package com.aaryan.coronavirustracker.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class TempUserInfo {

    @JsonProperty("UUID")
    private String UUID;
    @JsonProperty("id")
    private Integer id;
}
