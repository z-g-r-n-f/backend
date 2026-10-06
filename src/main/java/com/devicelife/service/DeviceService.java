package com.devicelife.service;

import com.devicelife.dto.DeviceRequest;
import com.devicelife.entity.Device;
import com.devicelife.exception.ResourceNotFoundException;
import com.devicelife.repository.DeviceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DeviceService {

    private final DeviceRepository deviceRepository;

    public List<Device> getDevicesByUser(Long userId) {
        return deviceRepository.findByUserId(userId);   // ← 注意 UserId 大小写
    }

    public Device getDevice(Long deviceId) {
        return deviceRepository.findById(deviceId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "기기가 존재하지 않습니다. id=" + deviceId));
    }

    @Transactional
    public Device createDevice(Long userId, DeviceRequest r) {
        Device device = new Device();
        device.setUserId(userId);
        applyRequest(device, r);
        return deviceRepository.save(device);
    }

    @Transactional
    public Device updateDevice(Long deviceId, DeviceRequest r) {
        Device device = getDevice(deviceId);
        applyRequest(device, r);
        return deviceRepository.save(device);
    }

    @Transactional
    public void deleteDevice(Long deviceId) {
        if (!deviceRepository.existsById(deviceId)) {
            throw new ResourceNotFoundException("기기가 존재하지 않습니다. id=" + deviceId);
        }
        deviceRepository.deleteById(deviceId);
    }

    private void applyRequest(Device d, DeviceRequest r) {
        d.setDeviceName(r.getDeviceName());
        d.setDeviceType(r.getDeviceType());
        d.setManufacturer(r.getManufacturer());
        d.setModel(r.getModel());
        d.setSerialNumber(r.getSerialNumber());
        d.setOperatingSystem(r.getOperatingSystem());
        d.setCpu(r.getCpu());
        d.setRamGb(r.getRamGb());
        d.setStorageGb(r.getStorageGb());
        d.setPurchaseDate(r.getPurchaseDate());
        d.setPurchasePrice(r.getPurchasePrice());
        d.setCurrentValue(r.getCurrentValue());
        if (r.getStatus() != null) d.setStatus(r.getStatus());
    }
}