library("lpSolve", lib.loc="~/R/x86_64-pc-linux-gnu-library/3.4")
library("lpSolveAPI", lib.loc="~/R/x86_64-pc-linux-gnu-library/3.4")
library("parallel", lib.loc="/usr/lib/R/library")

library(readr)
args <- commandArgs(trailingOnly = TRUE)
C <- read_csv(paste0(args,"data/C.csv"), col_names = FALSE)
write.csv(C, file = paste0(args,"data/RcMat.csv"))

MBT <- read_csv(paste0(args,"data/MBT.csv"), col_names = FALSE)
MNBT <- read_csv(paste0(args,"data/MNBT.csv"), col_names = FALSE)
DRLT <- read_csv(paste0(args,"data/DRLT.csv"), col_names = FALSE)
DRHT <- read_csv(paste0(args,"data/DRHT.csv"), col_names = FALSE)
constMat <- rbind(MBT,MNBT,DRLT,DRHT)
write.csv(constMat, file = paste0(args,"data/RconstMat.csv"))

MBTdir <- read_csv(paste0(args,"data/MBTdir.csv"), col_names = FALSE)
MNBTdir <- read_csv(paste0(args,"data/MNBTdir.csv"), col_names = FALSE)
DRLTdir <- read_csv(paste0(args,"data/DRLTdir.csv"), col_names = FALSE)
DRHTdir <-  read_csv(paste0(args,"data/DRHTdir.csv"), col_names = FALSE)
constDir <- rbind(t(MBTdir),t(MNBTdir),t(DRLTdir),t(DRHTdir))
write.csv(constDir, file = paste0(args,"data/RconstDir.csv"))

MBTrhs <- read_csv(paste0(args,"data/MBTrhs.csv"), col_names = FALSE)
MNBTrhs <- read_csv(paste0(args,"data/MNBTrhs.csv"), col_names = FALSE)
DRLTrhs <- read_csv(paste0(args,"data/DRLTrhs.csv"), col_names = FALSE)
DRHTrhs <- read_csv(paste0(args,"data/DRHTrhs.csv"), col_names = FALSE)
constRhs <- rbind(t(MBTrhs),t(MNBTrhs),t(DRLTrhs),t(DRHTrhs))
write.csv(constRhs, file = paste0(args,"data/RconstRhs.csv"))

SOL <- lp("min", t(C), constMat, constDir, constRhs, all.bin = TRUE)$solution
write.csv(SOL, file = paste0(args,"data/RlpSol.csv"))
