package ua.com.alevel.facade.user.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.WebRequest;
import ua.com.alevel.dto.user.PersonalAddressDto;
import ua.com.alevel.dto.user.PersonalInfoDto;
import ua.com.alevel.dto.user.PersonalPasswordDto;
import ua.com.alevel.exceptions.PasswordEnteredDoesNotMatchWithCurrentUserPasswordException;
import ua.com.alevel.exceptions.PasswordNewAndPasswordNewRepeatAreDifferentException;
import ua.com.alevel.facade.user.PersonalFacade;
import ua.com.alevel.persistence.entity.user.Personal;
import ua.com.alevel.service.user.PersonalService;

import java.time.LocalDate;
import java.util.Map;

@Slf4j
@Service
public class PersonalFacadeImpl implements PersonalFacade {

    private final PersonalService personalService;

    public PersonalFacadeImpl(PersonalService personalService) {
        this.personalService = personalService;
    }

    @Override
    public Personal findByEmail(String email) {
        return personalService.findByEmail(email);
    }

    @Override
    public PersonalInfoDto findPersonalInfo(Personal personal) {
        return new PersonalInfoDto(personal);
    }

    @Override
    public PersonalPasswordDto findPersonalPassword(Personal personal) {
        return new PersonalPasswordDto(personal);
    }

    @Override
    public PersonalAddressDto findPersonalAddress(Personal personal) {
        return new PersonalAddressDto(personal);
    }

    @Override
    public boolean changeInfo(PersonalInfoDto infoDto,
                              PersonalPasswordDto passwordDto,
                              PersonalAddressDto addressDto, WebRequest webRequest) {
        Long id = infoDto.getId();
        Personal personalCurrent = personalService.findById(id);
        log.info("Change personal data for user with id " + id);
        Map<String, String[]> map = webRequest.getParameterMap();
        if (MapUtils.isNotEmpty(map)) {
            String[] tabType = map.get("update");
            if (tabType != null) {
                String tabUpdated = tabType[0];
                switch (tabUpdated) {
                    case "info": updateInfo(personalCurrent, infoDto); break;
                    case "pass": updatePassword(personalCurrent, passwordDto); break;
                    case "address": updateAddress(personalCurrent, addressDto); break;
                }
            }
        }
        try {
            personalService.changeInfo(personalCurrent);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateInfo(Personal personal, PersonalInfoDto dto) {
        log.info("update info: firstName - " + dto.getFirstName() +
                ", lastName - " + dto.getLastName() +
                ", phoneNumber - " + dto.getPhoneNumber() +
                ", birthdayDate - " + dto.getBirthDay());
        personal.setFirstName(dto.getFirstName());
        personal.setLastName(dto.getLastName());
        personal.setPhoneNumber(dto.getPhoneNumber());
        if (dto.getBirthDay() != null && !dto.getBirthDay().equals("")) {
            personal.setBirthDay(LocalDate.parse(dto.getBirthDay()));
        } else {
            log.warn("birthday field is null or empty");
        }
    }

    private void updatePassword(Personal personal, PersonalPasswordDto dto) {
        String oldPassword = personal.getPassword();
        String oldPasswordCheck = dto.getPasswordOldCheck();
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        if (!encoder.matches(oldPasswordCheck, oldPassword)) {
            log.error("wrong old password check");
            throw new PasswordEnteredDoesNotMatchWithCurrentUserPasswordException("Wrong old password!");
        } else {
            String newPassword = dto.getPasswordNew();
            String newPasswordCheck = dto.getPasswordNewRepeat();
            if (!newPassword.equals(newPasswordCheck)) {
                log.error("wrong new password check");
                throw new PasswordNewAndPasswordNewRepeatAreDifferentException("Different values of new password!");
            } else {
                log.info("update password successful");
                personal.setPassword(encoder.encode(dto.getPasswordNew()));
            }
        }
    }

    private void updateAddress(Personal personal, PersonalAddressDto dto) {
        log.info("update address: zip - " + dto.getZip() +
                ", region - " + dto.getRegion() +
                ", city - " + dto.getCity() +
                ", street - " + dto.getStreet() +
                ", building - " + dto.getBuilding() +
                ", apartment - " + dto.getApartment());
        personal.setRegion(dto.getRegion());
        personal.setCity(dto.getCity());
        personal.setZip(dto.getZip());
        personal.setStreet(dto.getStreet());
        personal.setBuilding(dto.getBuilding());
        personal.setApartment(dto.getApartment());
    }
}
