package com.netology.diplom.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.netology.diplom.entity.CloudFile;
import com.netology.diplom.entity.User;

public interface FileRepository extends JpaRepository<CloudFile,Long> {

    void deleteByName (String name);

    List<CloudFile> findByUser(User user);
    //TODO to refactor
    @Query(value = "select * from cloud_file s where s.user_id = ?1 order by s.id desc limit ?2", nativeQuery = true)
    List<CloudFile> findAllByUserIdWithLimit(long userId, int limit);

    Optional<CloudFile> findByNameAndUser(String name, User user);

}

