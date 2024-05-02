-- PLACE
INSERT INTO place (ID, PLACE_ID, NAME, SUB_NAME, DESCRIPTION, PLACE_TYPE, LAT, LNG, PHOTO_URL) VALUES
                                                                                                   (1, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937, 'http://example.com/seoul.jpg'),
                                                                                                   (2, 'ChIJWw9PleHlYTURRh09nFHGt4A', '강릉', '강릉', '강릉입니당~~', 'CITY', 37.7091295, 128.8324462, 'http://example.com/gangneung.jpg'),
                                                                                                   (3, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '부산', '부산', '부산입니당~~', 'CITY', 35.2100142, 129.0688702, 'http://example.com/busan.jpg'),
                                                                                                   (4, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937, 'http://example.com/seoul2.jpg'),
                                                                                                   (5, 'ChIJWw9PleHlYTURRh09nFHGt4A', '강릉', '강릉', '강릉입니당~~', 'CITY', 37.7091295, 128.8324462, 'http://example.com/gangneung2.jpg'),
                                                                                                   (6, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '부산', '부산', '부산입니당~~', 'CITY', 35.2100142, 129.0688702, 'http://example.com/busan2.jpg'),
                                                                                                   (7, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937, 'http://example.com/seoul3.jpg'),
                                                                                                   (8, 'ChIJWw9PleHlYTURRh09nFHGt4A', '강릉', '강릉', '강릉입니당~~', 'CITY', 37.7091295, 128.8324462, 'http://example.com/gangneung3.jpg'),
                                                                                                   (9, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '부산', '부산', '부산입니당~~', 'CITY', 35.2100142, 129.0688702, 'http://example.com/busan3.jpg'),
                                                                                                   (10, 'ChIJzzlcLQGifDURm_JbQKHsEX4', '서울', '서울', '서울입니당~~', 'CITY', 37.5518911, 126.9917937, 'http://example.com/seoul4.jpg'),
                                                                                                   (11, 'ChIJoTpGnwaxfDUR6kl-yfyy6R0', '구리 한강시민 코스모스 축제', '경기도 구리시', '코스모스 축제입니당~~', 'THEME', 37.575408, 127.1395095, 'http://example.com/guri-cosmos-festival.jpg'),
                                                                                                   (12, 'ChIJ9SUiOHBOZjUR_YnH8Lbjzt0', '동궁과 월지(안압지)', '경상북도 경주시', '안압지입니당~~ 야경이 예뻐요', 'TRAVEL_PLACE', 35.8341593, 129.2265835, 'http://example.com/donggung-wolji.jpg');

-- PLACE_DETAIL
INSERT INTO place_detail (id, place_id, description, place_type) VALUES
                                                                     (1, 'ChIJ9SUiOHBOZjUR_YnH8Lbjzt0', '첫 번째 장소임', 'TRAVEL_PLACE'),
                                                                     (2, 'ChIJNc0j6G3raDURpwhxJHTL2DU', '두 번째 장소 부산', 'CITY'),
                                                                     (3, 'ChIJoTpGnwaxfDUR6kl-yfyy6R0', '세 번째 장소 구리', 'TRAVEL_PLACE'),
                                                                     (4, 'ChIJWw9PleHlYTURRh09nFHGt4A', '네 번째 장소 강릉', 'CITY');

-- PLACE_DETAIL - photoUrls
INSERT INTO place_detail_photo_urls (place_detail_id, photo_urls) VALUES
                                                                      (1, 'photo_url_1_for_place_1'),
                                                                      (1, 'photo_url_2_for_place_1');
INSERT INTO place_detail_photo_urls (place_detail_id, photo_urls) VALUES
                                                                      (2, 'photo_url_1_for_place_2');
-- TAG
INSERT INTO tag (id, name, tag_type) VALUES
                                         (1, '#야경명소', 'TRAVEL_PLACE'),
                                         (2, '#예쁨', 'TRAVEL_PLACE'),
                                         (3, '#데이트_필수_코스', 'TRAVEL_PLACE'),
                                         (4, '#해운대', 'TRAVEL_PLACE'),
                                         (5, '#힐링', 'TRAVEL_PLACE'),
                                         (6, '#돼지국밥', 'TRAVEL_PLACE'),
                                         (7, '#채은언니_사는_곳', 'TRAVEL_PLACE'),
                                         (8, '#뭐가_유명하지', 'TRAVEL_PLACE'),
                                         (9, '#경포해변', 'TRAVEL_PLACE'),
                                         (10, '#여름에_딱_좋아', 'TRAVEL_PLACE'),
                                         (11, '#무서운게딱좋아', 'TRAVEL_PLACE');

-- placeDetail + tag
INSERT INTO place_detail_tag (place_detail_id, tag_id) VALUES (1, 1), (1, 2), (1, 3);
INSERT INTO place_detail_tag (place_detail_id, tag_id) VALUES (2, 4), (2, 5), (2, 6);
INSERT INTO place_detail_tag (place_detail_id, tag_id) VALUES (3, 7), (3, 8);
INSERT INTO place_detail_tag (place_detail_id, tag_id) VALUES (4, 9), (4, 10), (4, 11);