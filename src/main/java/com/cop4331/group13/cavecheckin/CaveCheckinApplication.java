package com.cop4331.group13.cavecheckin;

import com.cop4331.group13.cavecheckin.api.dto.user.TaResponseDto;
import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.config.EncryptionUtil;
import com.cop4331.group13.cavecheckin.domain.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class CaveCheckinApplication {

	public static void main(String[] args) {
		SpringApplication.run(CaveCheckinApplication.class, args);
	}

	@Bean
	public ModelMapper modelMapper() {
		ModelMapper mapper = new ModelMapper();
		EncryptionUtil util = new EncryptionUtil();

		Converter<String, String> kioskPinEncryptor = mappingContext -> mappingContext.getSource() == null ? null : util.encrypt(mappingContext.getSource());

		PropertyMap<User, UserResponseDto> userDtoMap = new PropertyMap<User, UserResponseDto>() {
			@Override
			protected void configure() {
				using(kioskPinEncryptor).map(source.getKioskPin()).setKioskPin(null);
			}
		};

		PropertyMap<User, TaResponseDto> taDtoMap = new PropertyMap<User, TaResponseDto>() {
			@Override
			protected void configure() {
				using(kioskPinEncryptor).map(source.getKioskPin()).setKioskPin(null);
			}
		};

		mapper.addMappings(userDtoMap);
		mapper.addMappings(taDtoMap);

		return mapper;
	}

}
