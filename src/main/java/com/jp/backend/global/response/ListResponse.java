package com.jp.backend.global.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class ListResponse<T> {
	List<T> data;
}
