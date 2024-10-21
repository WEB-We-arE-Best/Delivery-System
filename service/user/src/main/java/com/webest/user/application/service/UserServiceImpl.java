package com.webest.user.application.service;

import com.webest.app.address.csv.ReadAddressCsv;
import com.webest.app.address.service.AddressDto;
import com.webest.user.domain.model.User;
import com.webest.user.domain.model.vo.OrderProductDto;
import com.webest.user.domain.model.vo.ShoppingCartDto;
import com.webest.user.domain.model.vo.UserDto;
import com.webest.user.domain.repository.UserRepository;
import com.webest.user.exception.UserErrorCode;
import com.webest.user.exception.UserException;
import com.webest.user.infrastructure.geocode.GoogleMapResponse;
import com.webest.user.infrastructure.redis.RedisUtil;
import com.webest.user.presentation.dto.request.UserJoinRequest;
import com.webest.user.presentation.dto.request.UserUpdateRequest;
import com.webest.user.presentation.dto.response.OrderProductResponse;
import com.webest.user.presentation.dto.response.UserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ReadAddressCsv readAddressCsv;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final RedisUtil redisUtil;

    @Value("${google.map.key}")
    private Object API_KEY;// 실제 서버에서 구동할때는 무조건 환경변수에 숨겨야함 절대 노출되면 안됨!!!

    // findByUserId
    public User findByUserId(String userId){
            return userRepository.findByUserId(userId)
                .orElseThrow(() -> new UserException(UserErrorCode.USER_NOT_FOUND));
    }

    // 위/경도 구하는 중복 코드 분리
    public double[] getGeocode(String address) {
        double[] result = new double[2];

        UriComponents uri = UriComponentsBuilder.newInstance()          // UriComponentsBuilder.newInstance = uri 주소를 직접 조립하여 만든다
                // https://maps.googleapis.com/maps/api/geocode/json?address="address"&key="API_KEY"와 같음
                // 위 처럼 한번에 사용하지 않고 조립해서 사용하는 이유는 address나 key값처럼 외부에서 값을 받아올때 쉽게 넣어 조립이 가능하기 때문
                .scheme("https")
                .host("maps.googleapis.com")
                .path("/maps/api/geocode/json")
                .queryParam("key",API_KEY)
                .queryParam("address",address)
                .build();

        GoogleMapResponse response = new RestTemplate().getForEntity(uri.toUriString(), GoogleMapResponse.class).getBody();     // 구글 map api에서 반환해주는 json형식을 MapResponse클래스 형식에 맞춰 받아옴
        result[0] = Arrays.stream(response.getResult()).findFirst().get().getGeometry().getLocation().getLat();
        result[1] =Arrays.stream(response.getResult()).findFirst().get().getGeometry().getLocation().getLng();

        return result;
    }


    // 유저 생성
    @Override
    @Transactional
    public UserResponse create(UserJoinRequest request) {

        // 위/경도 구함
        String address = request.city() + " "+request.street() + " "+ request.district() + " "+ request.detailAddress();
        double[] tmp = getGeocode(address);

        // TODO :: null 일때 에러 처리 필요
        AddressDto addressDto = readAddressCsv.findAddressByDistrict(request.city(),request.street(),request.district());
        UserDto dto = UserDto.from(request,bCryptPasswordEncoder.encode(request.password()),addressDto.code(),tmp[0],tmp[1]);
        userRepository.save(User.from(dto));
        return dto.to();
    }

    // 유저 마이페이지 데이터 호출
    @Override
    @Transactional(readOnly = true)
    public UserResponse getUser(String userId) {
        UserDto dto = findByUserId(userId).to();

        return dto.to();
    }

    // 유저 정보 수정
    @Override
    @Transactional
    public UserResponse update(String userId, UserUpdateRequest request) {
        User user = findByUserId(userId);

        String city = user.getCity();
        String street = user.getStreet();
        String district = user.getDistrict();
        String detailAddress = user.getDetailAddress();
        int count =0;

        if(request.city()!=null){
            city = request.city();
            count++;
        }
        if(request.street()!=null){
            street = request.street();
            count++;
        }
        if(request.district()!=null){
            district = request.district();
            count++;
        }
        if(request.detailAddress()!=null){
            detailAddress = request.detailAddress();
            count++;
        }


        // 주소가 변경되었으면 위/경도 구함
        double[] tmp = new double[2];
        if(count != 0){
            String address = city + " "+street+ " "+ district + " "+ detailAddress;
            tmp = getGeocode(address);
        }

        AddressDto addressDto = readAddressCsv.findAddressByDistrict(city,street,district);
        user.update(request,addressDto.code(),tmp[0],tmp[1]);

        return user.to().to();
    }

    // 유저 정보 삭제
    @Override
    @Transactional
    public void delete(Long userId,String xUserId) {
        findByUserId(xUserId);
        userRepository.delete(userId);
    }

    // 장바구니 출력
    @Override
    public OrderProductResponse getCart(String userId) {
        ShoppingCartDto dto = redisUtil.getShoppingCart(userId);
        OrderProductResponse response = new OrderProductResponse(dto.storeId(),dto.product());
        return response;
    }

    // 유저 전부 출력 (관리자 기능)
    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> getUserByAll() {
        Iterable<User> users = userRepository.findAll();
        List<UserResponse> result = new ArrayList<>();

        users.forEach(u -> {
            result.add(u.to().to());
        });

        return result;
    }
}
