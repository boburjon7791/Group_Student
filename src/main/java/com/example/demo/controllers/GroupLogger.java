package com.example.demo.controllers;

import com.example.demo.entities.Groups;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GroupLogger {
    private static final Logger logger = LoggerFactory.getLogger(GroupLogger.class);

    public static void updateEntity(Groups entity) {
        // Entityni yangilash logi
        logger.info("Entity yangilandi: {}", entity);

        // Yangilash logi boshqa ma'lumotlar bilan ham yozilishi mumkin
        logger.debug("Entity yangilandi: {} -> {}", entity.getId(), entity.getName());

        // Xatolarni log qilish uchun error darajasi ham ishlatilishi mumkin
        try {
            // Entityni yangilash logi
            logger.info("Entity yangilandi: {}", entity);

            // Yangilash logi boshqa ma'lumotlar bilan ham yozilishi mumkin
            logger.debug("Entity yangilandi: {} -> {}", entity.getId(), entity.getName());
        } catch (Exception e) {
            logger.error("Entity yangilashda xatolik yuz berdi", e);
        }
    }

    public static void deleteEntity(Groups entity) {
        // Entityni o'chirish logi
        logger.info("Entity o'chirildi: {}", entity);

        // O'chirish logi boshqa ma'lumotlar bilan ham yozilishi mumkin
        logger.debug("Entity o'chirildi: {} -> {}", entity.getId(), entity.getName());
    }
}
