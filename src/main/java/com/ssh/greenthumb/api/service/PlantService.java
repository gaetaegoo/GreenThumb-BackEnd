package com.ssh.greenthumb.api.service;

import com.ssh.greenthumb.api.common.exception.BadRequestException;
import com.ssh.greenthumb.api.common.exception.NotFoundException;
import com.ssh.greenthumb.api.dao.plant.PlantRepository;
import com.ssh.greenthumb.api.dao.user.UserRepository;
import com.ssh.greenthumb.api.domain.plant.Plant;
import com.ssh.greenthumb.api.domain.user.User;
import com.ssh.greenthumb.api.dto.plant.PlantDTO;
import com.ssh.greenthumb.auth.filter.TokenAuthenticationFilter;
import com.ssh.greenthumb.auth.token.TokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class PlantService {

    private final UserRepository userDao;
    private final PlantRepository plantDao;
    private final TokenProvider provider;
    private final TokenAuthenticationFilter filter;

    // 식물 생성
    @Transactional
    public Long add(PlantDTO.Create dto) {
        User user = userDao.findById(dto.getUserId()).
                orElseThrow(NotFoundException::new);

        return plantDao.save(dto.toEntity(user, dto.getName(), dto.getNickName(),
                dto.getWater(), dto.getTemp(), dto.getImageUrl())).getId();
    }

    // 전체 식물 조회
    @Transactional
    public List<PlantDTO.Get> getAll() {
        return plantDao.findAll().stream().map(PlantDTO.Get::new).collect(Collectors.toList());
    }

    // 유저별 식물 조회(전체)
    @Transactional
    public List<PlantDTO.Get> getAllByUser(Long userId, HttpServletRequest request) {
        String token = filter.getJwtFromRequest(request);

        if(userId != provider.getUserIdFromToken(token).longValue()) {
            throw new BadRequestException("잘못된 아이디 요청");
        } else {
            User user = userDao.findById(userId).
                    orElseThrow(NotFoundException::new);

            return plantDao.findAllByUser(user).stream().map(PlantDTO.Get::new).collect(Collectors.toList());
        }
    }

    // 유저별 식물 조회(하나)
    @Transactional
    public PlantDTO.Get getOneByUser(Long plantId) {
        return plantDao.findById(plantId).map(PlantDTO.Get::new).get();
    }

    // 식물 수정
    @Transactional
    public Long update(Long plantId, PlantDTO.Update dto) {
        Plant plant = plantDao.findById(plantId).
                orElseThrow(NotFoundException::new);

        plant.update(dto.getName(), dto.getNickName(), dto.getWater(),
                dto.getTemp(), dto.getImageUrl());

        return plantId;
    }

    // 식물 삭제
    @Transactional
    public void delete(Long plantId) {
        Plant plant = plantDao.findById(plantId).
                orElseThrow(NotFoundException::new);

        plantDao.delete(plant);
    }

}