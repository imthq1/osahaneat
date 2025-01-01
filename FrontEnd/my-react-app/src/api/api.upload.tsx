const BASE_URL = "http://localhost:8080/api/v1";

class Upload {
  async uploadImage(file: File, folder: string): Promise<any> {
    const formData = new FormData();
    formData.append("file", file);
    formData.append("folder", folder);

    const response = await fetch(`${BASE_URL}/upload/image`, {
      method: "POST",

      body: formData,
    });

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
        errorData.message || "Failed to upload the image. Please try again."
      );
    }

    return await response.json();
  }
  async loadImage(id: number, folder: string): Promise<any> {
    const response = await fetch(
      `${BASE_URL}/restaurant/load/${id}?folder=${encodeURIComponent(folder)}`,
      {
        method: "POST",
      }
    );

    if (!response.ok) {
      const errorData = await response.json();
      throw new Error(
        errorData.message || "Failed to upload the image. Please try again."
      );
    }

    return await response.json();
  }
}

export const upload = new Upload();
