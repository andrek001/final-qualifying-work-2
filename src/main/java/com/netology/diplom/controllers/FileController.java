package com.netology.diplom.controllers;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.netology.diplom.entity.CloudFile;
import com.netology.diplom.exceptions.FileStorageException;
import com.netology.diplom.service.FileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
public class FileController {

	private final FileService fileService;

	@GetMapping("/list")
	public List<CloudFile> getFileList(@RequestParam("limit") Integer limit) {
		return fileService.find(limit);
	}

	@PostMapping("/file")
	public ResponseEntity<Void> saveFile(String fileName, MultipartFile file) throws Exception {
		fileService.uploadFile(fileName, file);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@DeleteMapping("/file")
	public ResponseEntity<Void> deleteFile(@RequestParam("fileName") String fileName) throws Exception {
		fileService.deleteFile(fileName);
		return ResponseEntity.noContent().build();
	}

	@GetMapping("/file")
	public byte[] getFile(@RequestParam("fileName") String fileName) {
		return fileService.getFileByName(fileName)
				.orElseThrow(() -> new FileStorageException("Файл не найден"))
				.getData();
	}

	@PutMapping("/file")
	public ResponseEntity<Void> editFile(@RequestParam("filename") String fileName, MultipartFile file)
			throws IOException {
		CloudFile cloudFile = fileService.getFileByName(fileName)
				.orElseThrow(() -> new FileNotFoundException("Файл не найден"));
		fileService.uploadFile(fileName, file, cloudFile);
		return ResponseEntity.accepted().build();
	}
}




