package com.example.uploaderservice.repository;

import com.example.uploaderservice.model.Doc;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DocRepository extends JpaRepository<Doc,Integer> {

}
