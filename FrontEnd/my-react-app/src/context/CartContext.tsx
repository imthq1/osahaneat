import React, { createContext, useContext, useState } from "react";

// Export the Food type
export type Food = {
  id: number;
  name: string;
  price: number;
  quantity: number;
  logo: string;
  quantityInCart?: number; // Make quantityInCart optional
};

type CartItem = Food & { quantityInCart: number };

type CartContextType = {
  cart: CartItem[];
  addToCart: (item: Food) => void;
  removeFromCart: (id: number) => void;
  clearCart: () => void;
  totalItems: number;
};

const CartContext = createContext<CartContextType | undefined>(undefined);

export const CartProvider: React.FC<{ children: React.ReactNode }> = ({
  children,
}) => {
  const [cart, setCart] = useState<CartItem[]>([]);

  const addToCart = (item: Food) => {
    setCart((prev) => {
      const existing = prev.find((i) => i.id === item.id);
      if (existing) {
        const change = item.quantityInCart || 1; // Default to +1 if not specified
        if (change < 0) {
          // Decrease quantity by the absolute value of the change
          const newQuantity = Math.max(0, existing.quantityInCart + change); // Prevent negative quantity
          if (newQuantity === 0) {
            return prev.filter((i) => i.id !== item.id); // Remove if quantity becomes 0
          }
          return prev.map((i) =>
            i.id === item.id ? { ...i, quantityInCart: newQuantity } : i
          );
        }
        // Increase quantity by the change value (default 1)
        return prev.map((i) =>
          i.id === item.id
            ? { ...i, quantityInCart: existing.quantityInCart + change }
            : i
        );
      }
      // Add new item, default quantity to 1 if not specified
      return [...prev, { ...item, quantityInCart: item.quantityInCart || 1 }];
    });
  };

  const removeFromCart = (id: number) => {
    setCart((prev) => prev.filter((item) => item.id !== id));
  };

  const clearCart = () => setCart([]);

  const totalItems = cart.reduce((sum, item) => sum + item.quantityInCart, 0);

  return (
    <CartContext.Provider
      value={{ cart, addToCart, removeFromCart, clearCart, totalItems }}
    >
      {children}
    </CartContext.Provider>
  );
};

export const useCart = () => {
  const context = useContext(CartContext);
  if (!context) throw new Error("useCart must be used within CartProvider");
  return context;
};
