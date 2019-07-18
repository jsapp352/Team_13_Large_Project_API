package com.cop4331.group13.cavecheckin;

import com.cop4331.group13.cavecheckin.api.dto.user.UserResponseDto;
import com.cop4331.group13.cavecheckin.config.EncryptionUtil;
import com.cop4331.group13.cavecheckin.domain.User;
import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.modelmapper.TypeMap;
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
		ModelMapper mapper =  new ModelMapper();
		EncryptionUtil util = new EncryptionUtil();
		mapper.addMappings(new PropertyMap<User, UserResponseDto>() {
			@Override
			protected void configure() {
				map().setKioskPin(Long.parseLong(util.Encrypt(source.getKioskPin())));
			}
		});

		return mapper;
	}

}
