export interface IRole {
  id: number;
  roleName: string;
}

export interface IUser {
  id: number;
  fullname: string;
  email: string;
  address: string;
  enable: string | null;
  role: IRole;
}
