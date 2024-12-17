package com.netology.diplom.service;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.netology.diplom.entity.CloudFile;
import com.netology.diplom.entity.User;
import com.netology.diplom.exceptions.UserNotFoundException;
import com.netology.diplom.repository.FileRepository;

@Service
@Slf4j
public class FileService {
	private final FileRepository fileRepository;
	private final UserService userService;

	public FileService(FileRepository fileRepository, UserService userService) {
		this.fileRepository = fileRepository;
		this.userService = userService;
	}

	public void uploadFile(String filename,
			MultipartFile file) throws IOException {
		uploadFile(filename, file, null);
	}

	public Optional<CloudFile> getFileByName(String fileName) {
		Optional<User> userOptional = userService.findByLogin(getAuthenticationLogin());
		if (userOptional.isEmpty()) {
			log.error("User not found");
			throw new UserNotFoundException("User not found");
		}
		return fileRepository.findByNameAndUser(fileName, userOptional.get());

	}

	public void deleteFile(String fileName) {
		fileRepository.deleteByName(fileName);
	}

	public void uploadFile(String filename,
			MultipartFile file, CloudFile uploadFile) throws IOException {

		String login = SecurityContextHolder.getContext().getAuthentication().getName();
		Optional<User> optionalUser = userService.findByLogin(login);
		if (optionalUser.isPresent()) {
			if (uploadFile == null) {
				uploadFile = new CloudFile();
			}
			uploadFile.setName(filename);
			uploadFile.setData(file.getBytes());
			uploadFile.setSize(file.getSize());
			uploadFile.setUser(optionalUser.get());

			fileRepository.saveAndFlush(uploadFile);
		} else {
			throw new UserNotFoundException("User not found");
		}
	}

	public List<CloudFile> find(Integer limit) {

		String name = getAuthenticationLogin();
		User user = userService.findByLogin(name)
				.orElseThrow(() -> new UserNotFoundException("User not found"));
		return limit == null ?
				fileRepository.findByUser(user) :
				fileRepository.findAllByUserIdWithLimit(user.getId(), limit);
	}

	public String getAuthenticationLogin() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}
}









