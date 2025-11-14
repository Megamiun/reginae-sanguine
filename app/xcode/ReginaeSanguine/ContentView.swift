//
//  ContentView.swift
//  reginaesanguine
//
//  Created by Gabryel Monteiro on 28/08/2025.
//

import UIKit
import SwiftUI
import ReginaeSanguineCompose

struct ContentView: View {
    var body: some View {
        ComposeView().ignoresSafeArea(.all)
    }
}

struct ComposeView: UIViewControllerRepresentable {
    func makeUIViewController(context: Context) -> UIViewController {
        IosMainKt.mainViewController()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}
