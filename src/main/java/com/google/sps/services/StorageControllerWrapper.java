package com.google.sps.services;

/**
 * @code StorageControllerWrapper is used to share
 * one entity of the @code StorageController among
 * all classes
 */
public class StorageControllerWrapper {
  private static final Object COMMON_STORAGE_CONTROLLER = null;

  public static Object getStorageController() {
    return COMMON_STORAGE_CONTROLLER;
  }
}
