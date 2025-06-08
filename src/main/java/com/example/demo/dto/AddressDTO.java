package com.example.demo.dto;

import com.example.demo.model.Address;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddressDTO {
    private String street;
    private String ward;
    private String district;
    private String province;

    public AddressDTO(Address address) {
        this.street = address.getStreet();
        this.ward = address.getWard();
        this.district = address.getDistrict();
        this.province = address.getProvince();
    }

}