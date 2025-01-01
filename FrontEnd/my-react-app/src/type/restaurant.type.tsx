// Định nghĩa các loại trạng thái
export enum Status {
  PENDING = "PENDING",
  APPROVED = "APPROVED",
  REJECTED = "REJECTED",
}

// Định nghĩa interface cho Image
export interface IImage {
  id: number;
  path: string;
}

// Định nghĩa interface cho Category (nếu có)
export interface ICategory {
  id: number;
  name: string;
}

// Định nghĩa interface cho User (nếu cần)
export interface IUser {
  id: number;
  email: string;
  fullname: string;
}

// Định nghĩa interface chính cho Restaurant
export interface IRestaurant {
  id: number;
  name: string;
  address: string;
  description: string;
  rating: number;
  logo: string;
  imageList: IImage[];
  content: string;
  status: Status;
  categories?: ICategory[];
  user?: IUser;
  logoUrl: string;
}
