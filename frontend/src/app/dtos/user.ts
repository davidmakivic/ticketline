
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
  rewardPoints: number;
}

export enum Roles {
  ADMIN='ADMIN',
  USER='USER'
}

export enum UserStatus{
  UNLOCKED='UNLOCKED',
  LOCKED='LOCKED',
  UNVERIFIED='UNVERIFIED'
}
