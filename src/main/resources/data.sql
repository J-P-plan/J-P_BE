INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (1, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (2, 'ChIJWw9PleHlYTURRh09nFHGt4A', '강릉', '강릉', '강릉입니당~~', 'CITY', 37.7091295, 128.8324462);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (3, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '부산', '부산', '부산입니당~~', 'CITY', 35.2100142, 129.0688702);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (4, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (5, 'ChIJWw9PleHlYTURRh09nFHGt4A', '강릉', '강릉', '강릉입니당~~', 'CITY', 37.7091295, 128.8324462);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (6, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '부산', '부산', '부산입니당~~', 'CITY', 35.2100142, 129.0688702);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (7, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (8, 'ChIJWw9PleHlYTURRh09nFHGt4A', '강릉', '강릉', '강릉입니당~~', 'CITY', 37.7091295, 128.8324462);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (9, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '부산', '부산', '부산입니당~~', 'CITY', 35.2100142, 129.0688702);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (10, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (11, 'ChIJoTpGnwaxfDUR6kl-yfyy6R0', '구리 한강시민 코스모스 축제', '경기도 구리시', '코스모스 축제입니당~~', 'THEME', 37.575408,
        127.1395095);
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG)
VALUES (12, 'ChIJ9SUiOHBOZjUR_YnH8Lbjzt0', '동궁과 월지(안압지)', '경상북도 경주시', '안압지입니당~~ 야경이 예뻐요', 'TRAVEL', 35.8341593,
        129.2265835);

INSERT INTO place_detail (id, place_id, description, place_type)
VALUES (1, 'ChIJ9SUiOHBOZjUR_YnH8Lbjzt0', '첫 번째 장소임', 'TRAVEL');

INSERT INTO place_detail (id, place_id, description, place_type)
VALUES (2, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '두 번째 장소 부산', 'CITY');

INSERT INTO place_detail (id, place_id, description, place_type)
VALUES (3, 'ChIJoTpGnwaxfDUR6kl-yfyy6R0', '세 번째 장소 구리', 'TRAVEL');

INSERT INTO place_detail (id, place_id, description, place_type)
VALUES (4, 'ChIJWw9PleHlYTURRh09nFHGt4A', '네 번째 장소 강릉', 'CITY');

-- PlaceDetail ID 1에 대한 태그 삽입
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (1, '#야경명소');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (1, '#예쁨');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (1, '#데이트_필수_코스');

-- PlaceDetail ID 2에 대한 태그 삽입
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (2, '#해운대');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (2, '#힐링');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (2, '#돼지국밥');

-- PlaceDetail ID 3에 대한 태그 삽입
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (2, '#채은언니_사는_곳');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (2, '#뭐가_유명하지');

-- PlaceDetail ID 4에 대한 태그 삽입
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (3, '#경포해변');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (3, '#여름에_딱_좋아');
INSERT INTO place_detail_tags (place_detail_id, tags) VALUES (3, '#무서운게딱좋아');


