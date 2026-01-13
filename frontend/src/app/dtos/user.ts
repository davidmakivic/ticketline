
export interface User {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  country: string;
  zipCode: string;
  address: string;
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
  address: string;
}

export interface UserDto {
  userId: number;
  email: string;
  firstName: string;
  lastName: string;
  country: string;
  zipCode: string;
  city: string;
  address: string;
  role: Roles;
  rewardPoints: number;
  createdAt: string;        // ISO string from backend
  userStatus: UserStatus;
  failedLoginAttempts: number;
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
