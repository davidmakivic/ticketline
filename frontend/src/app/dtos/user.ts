
export interface User {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  country: string;
  zipCode: string;
  street: string;
  houseNumber: number;
  role: string;
  rewardPoints: number;
}

export interface UserRegisterDto {
  firstName: string;
  lastName: string;
  email: string;
  password: string;
  country: string;
  zipCode: string;
  city: string;
  street: string;
  houseNumber: number;
}

export interface UserCreateDto {
  email: string;
  password: string;
  firstName: string;
  lastName: string;
  country: string;
  zipCode: string;
  city: string;
  street: string;
  houseNumber: number;
  role: Roles;
}

export interface UserUpdateDto {
  firstName: string;
  lastName: string;
  email: string;
  country: string;
  zipCode: string;
  city: string;
  street: string;
  houseNumber: number;
}

export interface UserDto {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  country: string;
  zipCode: string;
  city: string;
  street: string;
  houseNumber: number;
  role: Roles;
  rewardPoints: number;
  createdAt: string;        // ISO string from backend
  userStatus: UserStatus;
  failedLoginAttempts: number;
}

export interface PasswordChangeDto {
  oldPassword: string;
  newPassword: string;
  resetToken: string;
}

export enum Roles {
  ADMIN='ADMIN',
  USER='USER'
}

export enum UserStatus{
  UNLOCKED='UNLOCKED',
  LOCKED='LOCKED',
}
