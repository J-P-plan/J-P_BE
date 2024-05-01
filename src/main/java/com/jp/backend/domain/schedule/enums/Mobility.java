package com.jp.backend.domain.schedule.enums;

public enum Mobility {
	CAR("자동차"),
	BUS("버스/지하철"),
	TRAIN("기차"),
	TAXI("택시");
	private final String value;

	Mobility(String value) {
		this.value = value;
	}

}
