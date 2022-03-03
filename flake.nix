{
  outputs = { self, nixpkgs }: let 
    pkgs = nixpkgs.legacyPackages."x86_64-linux";
  in{
    devShell."x86_64-linux" = pkgs.mkShell {
      packages = with pkgs; [ sbt jdk bloop ];
    };
  };
}
